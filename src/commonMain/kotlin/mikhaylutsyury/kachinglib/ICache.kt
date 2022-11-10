package mikhaylutsyury.kachinglib

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asFlow

interface ICache<TKey, TValue> {
	var capacity: Int?
	suspend fun cached(): Iterable<Pair<TKey, TValue>>
	suspend fun cachedValues(): Iterable<TValue>
	suspend fun cachedKeys(): Iterable<TKey>
	suspend fun cachedFlow(): Flow<Pair<TKey, TValue>> = cached().asFlow()
	suspend fun cachedValuesFlow(): Flow<TValue> = cachedValues().asFlow()
	suspend fun cachedKeysFlow(): Flow<TKey> = cachedKeys().asFlow()
	val onUpdate: SharedFlow<Pair<TKey, TValue>>
	val onDrop: SharedFlow<Pair<TKey, TValue>>
	suspend fun get(key: TKey): TValue
	suspend fun clear()
	suspend fun drop(key: TKey): TValue?
}

