package mikhaylutsyury.kachinglib

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asFlow

interface ICache<TKey, TValue> {
	var capacity: Int?
	val cached: Iterable<Pair<TKey, TValue>>
	val cachedValues: Iterable<TValue>
	val cachedKeys: Iterable<TKey>
	val cachedFlow: Flow<Pair<TKey, TValue>> get() = cached.asFlow()
	val cachedValuesFlow: Flow<TValue> get() = cachedValues.asFlow()
	val cachedKeysFlow: Flow<TKey> get() = cachedKeys.asFlow()
	val onUpdate: SharedFlow<Pair<TKey, TValue>>
	val onDrop: SharedFlow<Pair<TKey, TValue>>
	suspend fun get(key: TKey): TValue
	suspend fun clear()
	suspend fun drop(key: TKey): TValue?
}

