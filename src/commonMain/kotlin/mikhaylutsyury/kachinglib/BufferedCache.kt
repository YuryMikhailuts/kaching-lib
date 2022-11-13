package mikhaylutsyury.kachinglib

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.toList

/**
 * Простой кеш на базе буфера конечного или бесконечного размера.
 * @param capacity размер буфера, если он конечен, или `null`, что бы создать кеш с буфером бесконечного размера.
 * @param dispatcher диспетчер корутин ([scope] : [CoroutineScope]) для отправки событий [onDrop] и [onUpdate]. По умолчанию равен [Dispatchers.Default].
 * @param getter `suspend` функция, которая должна будет возвращать оригинальные значения по ключу, если они отсутствуют в кеше.
 */
open class BufferedCache<TKey, TValue : Any?>(
	override var capacity: Int? = null,
	dispatcher: CoroutineDispatcher = Dispatchers.Default,
	protected val getterList: (suspend (Iterable<TKey>) -> Map<TKey, TValue>)?,
	protected val getListChunkedBy: UInt? = null,
	protected val getter: suspend (TKey) -> TValue,
) : BasicCache<TKey, TValue>(dispatcher), ICache<TKey, TValue> {
	protected val map = hashMapOf<TKey, TValue>()
	protected val mutex = Mutex()

	val getterListInt: suspend (Iterable<TKey>) -> Map<TKey, TValue> by lazy {
		getterList ?: { keys -> keys.asFlow().map { it to getter(it) }.toList().associate { it } }
	}
	val getList: suspend (Iterable<TKey>) -> Map<TKey, TValue> by lazy {
		val getListChunkedBy = getListChunkedBy
		if (getListChunkedBy == null) {
			getterListInt
		} else {
			val function: suspend (Iterable<TKey>) -> Map<TKey, TValue> = { keys: Iterable<TKey> ->
				keys.chunked(getListChunkedBy.toInt())
					.asFlow()
					.map { subKeys -> getterListInt(subKeys) }
					.reduce { acc, value -> acc + value }
			}
			function
		}
	}

	protected open class OrderItem<T>(val item: T, val lastUpdate: Instant = Clock.System.now()) :
			Comparable<OrderItem<T>> {
		@Suppress("RedundantNullableReturnType")
		override fun compareTo(other: OrderItem<T>): Int {
			// WARNING: Вся эта чехорда с насильной проверкой на null нужна по причине того, что некоторые классы в JS совершенно игнорируют null безопасность kotlin.
			val otherItem: OrderItem<T>? = other
			val thisItem: OrderItem<T>? = this
			return otherItem?.let { lastUpdate.compareTo(it.lastUpdate) } ?: (if (thisItem == null) 0 else 1)
		}
		override fun equals(other: Any?): Boolean = lastUpdate == (other as? OrderItem<*>?)?.lastUpdate
		override fun hashCode(): Int = lastUpdate.hashCode()
	}

	protected open fun <T> orderItem(item: T, lastUpdate: Instant = Clock.System.now()) = OrderItem(item, lastUpdate)

	protected val orderSet = MutableTreeSet<OrderItem<TKey>>()
	protected val orderMap = mutableMapOf<TKey, OrderItem<TKey>>()
	override suspend fun cached(): Iterable<Pair<TKey, TValue>> = mutex.withLock { map.toList() }
	override suspend fun cachedValues(): Iterable<TValue> = mutex.withLock { ArrayList(map.values) }
	override suspend fun cachedKeys(): Iterable<TKey> = mutex.withLock { ArrayList(map.keys) }
	override val onUpdate = MutableSharedFlow<Pair<TKey, TValue>>(0, 1, BufferOverflow.DROP_OLDEST)
	override val onDrop = MutableSharedFlow<Pair<TKey, TValue>>(0, 1, BufferOverflow.DROP_OLDEST)
	override suspend fun clear() {
		mutex.withLock {
			orderMap.clear()
			orderSet.clear()
			val pairs = map.toList()
			onDrop.emitAll(pairs.asFlow())
			map.clear()
		}
	}

	override suspend fun drop(key: TKey): TValue? = mutex.withLock {
		orderSet.remove(orderMap.remove(key))
		callOnDrop(key)
		map.remove(key)
	}

	protected open suspend fun updateOrder(key: TKey, value: TValue) {
		orderSet.remove(orderMap.remove(key))
		val orderItem = orderItem(key)
		orderMap[key] = orderItem
		orderSet.add(orderItem)
		val pair = key to value
		scope.launch { onUpdate.emit(pair) }
	}

	protected open suspend fun dropOldItems() {
		val capacity = this.capacity
		val capacityIsNull = capacity != null
		while (orderSet.isNotEmpty()) {
			val oldestItem = orderSet.first()
			if (capacityIsNull && capacity!! < map.size) {
				val key = oldestItem.item
				orderMap.remove(key)
				orderSet.remove(oldestItem)
				callOnDrop(key)
				map.remove(key)
			} else {
				break
			}
		}
	}

	protected suspend fun callOnDrop(key: TKey) {
		if (key in map) {
			val pair = key to map.getValue(key)
			scope.launch { onDrop.emit(pair) }
		}
	}

	@Suppress("DuplicatedCode")
	override suspend fun getAll(vararg keys: TKey): List<TValue> {
		val (localMap, remoteKeys) = mutex.withLock {
			val keysSet = keys.toSet()
			val localKeys = map.keys intersect keysSet
			val remoteKeys = keysSet - localKeys
			val localMap = localKeys.associateWith { map.getValue(it) }
			localMap.forEach { (key, value) -> updateOrder(key, value) }
			if (remoteKeys.isEmpty()) return keys.asFlow().map { localMap.getValue(it) }.toList()
			localMap to remoteKeys
		}
		val values = getList(remoteKeys)
		mutex.withLock {
			values.forEach { (key, value) -> updateOrder(key, value) }
			map.putAll(values)
			dropOldItems()
		}
		val result = localMap + values
		return keys.asFlow().map { result.getValue(it) }.toList()
	}

	override suspend fun get(key: TKey): TValue {
		mutex.withLock {
			if (key in map.keys) {
				val value = map.getValue(key)
				updateOrder(key, value)
				return value
			}
		}
		val value = getter(key)
		mutex.withLock {
			updateOrder(key, value)
			map[key] = value
			dropOldItems()
		}
		return value
	}

}