package mikhaylutsyury.kachinglib

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

class TimedCache<TKey, TValue : Any?>(
	var storageDuration: Duration,
	override var capacity: Int? = null,
	private val getter: suspend (TKey) -> TValue,
) : BasicCache<TKey, TValue>(), ICache<TKey, TValue> {
	private val map = hashMapOf<TKey, TValue>()
	private val mutex = Mutex()

	private data class OrderItem<T>(val item: T, val lastUpdate: Instant = Clock.System.now()) :
			Comparable<OrderItem<T>> {
		override fun compareTo(other: OrderItem<T>): Int = lastUpdate.compareTo(other.lastUpdate)
		override fun equals(other: Any?): Boolean = lastUpdate == (other as? OrderItem<*>?)?.lastUpdate
		override fun hashCode(): Int = lastUpdate.hashCode()
	}

	private val orderSet = MutableTreeSet<OrderItem<TKey>>()
	private val orderMap = mutableMapOf<TKey, OrderItem<TKey>>()
	override suspend fun cached(): Iterable<Pair<TKey, TValue>> = mutex.withLock { map.toList() }
	override suspend fun cachedValues(): Iterable<TValue> = mutex.withLock { ArrayList(map.values) }
	override suspend fun cachedKeys(): Iterable<TKey> = mutex.withLock { ArrayList(map.keys) }
	override val onUpdate = MutableSharedFlow<Pair<TKey, TValue>>(0, 1, BufferOverflow.DROP_OLDEST)
	override val onDrop = MutableSharedFlow<Pair<TKey, TValue>>(0, 1, BufferOverflow.DROP_OLDEST)
	override suspend fun clear() {
		mutex.withLock {
			orderMap.clear()
			orderSet.clear()
			onDrop.emitAll(map.toList().asFlow())
			map.clear()
		}
	}

	override suspend fun drop(key: TKey): TValue? = mutex.withLock {
		orderSet.remove(orderMap.remove(key))
		callOnDrop(key)
		map.remove(key)
	}

	private suspend fun updateOrder(key: TKey, value: TValue) {
		orderSet.remove(orderMap.remove(key))
		val orderItem = OrderItem(key)
		orderMap[key] = orderItem
		orderSet.add(orderItem)
		onUpdate.emit(key to value)
	}

	private suspend fun dropOldItems() {
		val capacity = this.capacity
		while (orderSet.isNotEmpty()) {
			val oldestItem = orderSet.first()
			val itemIsOldest = Clock.System.now() - oldestItem.lastUpdate > storageDuration
			val bufferOverflow = capacity != null && capacity < map.size
			if (itemIsOldest || bufferOverflow) {
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

	private suspend fun callOnDrop(key: TKey) {
		if (key in map) {
			onDrop.emit(key to map.getValue(key))
		}
	}

	private object NULL

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