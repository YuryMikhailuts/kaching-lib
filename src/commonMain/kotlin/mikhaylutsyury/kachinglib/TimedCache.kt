package mikhaylutsyury.kachinglib

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

class TimedCache<TKey, TValue>(
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
	override val cached: Iterable<Pair<TKey, TValue>> get() = map.toList()
	override val cachedValues: Iterable<TValue> get() = ArrayList(map.values)
	override val cachedKeys: Iterable<TKey> get() = ArrayList(map.keys)
	override val onUpdate = MutableSharedFlow<Pair<TKey, TValue>>(0, 0, BufferOverflow.DROP_OLDEST)
	override val onDrop = MutableSharedFlow<Pair<TKey, TValue>>(0, 0, BufferOverflow.DROP_OLDEST)
	override suspend fun clear() {
		mutex.withLock {
			map.clear()
			orderMap.clear()
			orderSet.clear()
		}
	}

	override suspend fun drop(key: TKey): TValue? = mutex.withLock {
		orderSet.remove(orderMap.remove(key))
		map.remove(key)
	}

	private fun updateOrder(key: TKey) {
		orderSet.remove(orderMap.remove(key))
		val orderItem = OrderItem(key)
		orderMap[key] = orderItem
		orderSet.add(orderItem)
	}

	private fun dropOldItems() {
		val capacity = this.capacity
		while (orderSet.isNotEmpty()) {
			val oldestItem = orderSet.first()
			val itemIsOldest = Clock.System.now() - oldestItem.lastUpdate > storageDuration
			val bufferOverflow = capacity != null && capacity < map.size
			if (itemIsOldest || bufferOverflow) {
				orderMap.remove(oldestItem.item)
				orderSet.remove(oldestItem)
				map.remove(oldestItem.item)
			} else {
				break
			}
		}
	}

	override suspend fun get(key: TKey): TValue {
		val value = mutex.withLock {
			if (key in map.keys) updateOrder(key)
			val value = map[key]
			value
		}
		return if (value == null) {
			val answer = getter(key)
			mutex.withLock {
				map[key] = answer
				dropOldItems()
			}
			answer
		} else {
			value
		}
	}

}