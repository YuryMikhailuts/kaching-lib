package mikhaylutsyury.kachinglib

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class TimedCache<TKey, TValue>(
	private val getter: suspend (TKey) -> TValue,
	override var capacity: Int? = null,
) : BasicCache<TKey, TValue>(), ICache<TKey, TValue> {
	private val map = hashMapOf<TKey, TValue>()
	private val mutex = Mutex()
	override val cached: Iterable<Pair<TKey, TValue>>
		get() = TODO("Not yet implemented")
	override val cachedValues: Iterable<TValue>
		get() = TODO("Not yet implemented")
	override val cachedKeys: Iterable<TKey>
		get() = map.keys
	override val onUpdate = MutableSharedFlow<Pair<TKey, TValue>>(0, 0, BufferOverflow.DROP_OLDEST)
	override val onDrop = MutableSharedFlow<Pair<TKey, TValue>>(0, 0, BufferOverflow.DROP_OLDEST)
	override fun clear() {
		map.clear()
	}

	override fun drop(key: TKey): TValue? = map.remove(key)

	override suspend fun get(key: TKey): TValue {

		val value = mutex.withLock { map[key] }
		return if (value == null) {
			val answer = getter(key)
			mutex.withLock { map[key] = answer }
			answer
		} else {
			value
		}
	}

}