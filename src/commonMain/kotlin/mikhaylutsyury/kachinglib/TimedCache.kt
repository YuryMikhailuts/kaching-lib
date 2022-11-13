package mikhaylutsyury.kachinglib

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlin.time.Duration

/**
 * Кеш на базе буфера конечного или бесконечного размера с возможностью выбрасывать элементы, которые не запрашивались дольше определённого времени.
 * При [storageDuration] равном `null` ведёт себя точно так же, как и [BufferedCache].
 * @param storageDuration время, в течение которого элемент хранится, если его никто не запрашивает. Элементы, которые хранятся дольше, чем [storageDuration] могу быть вытеснены более новыми.
 * @param capacity размер буфера, если он конечен, или `null`, что бы создать кеш с буфером бесконечного размера.
 * @param mode режим работы буфера. Выбирает один из режимов работы, описанных в [TimedCacheMode].
 * @param dispatcher диспетчер корутин ([scope] : [CoroutineScope]) для отправки событий [onDrop] и [onUpdate]. По умолчанию равен [Dispatchers.Default].
 * @param getter `suspend` функция, которая должна будет возвращать оригинальные значения по ключу, если они отсутствуют в кеше.
 * @see [TimedCacheMode].
 */
class TimedCache<TKey, TValue : Any?>(
	var storageDuration: Duration? = null,
	override var capacity: Int? = null,
	val mode: TimedCacheMode = TimedCacheMode.RelevanceMode,
	dispatcher: CoroutineDispatcher = Dispatchers.Default,
	getterList: (suspend (Iterable<TKey>) -> Map<TKey, TValue>)? = null,
	getListChunkedBy: UInt? = null,
	getter: suspend (TKey) -> TValue,
) : BufferedCache<TKey, TValue>(capacity, dispatcher, getterList, getListChunkedBy, getter), ICache<TKey, TValue> {
	override suspend fun dropOldItems() {
		val capacity = this.capacity
		val capacityIsNull = capacity != null
		val storageDuration = this.storageDuration
		val storageDurationIsNull = storageDuration != null
		while (orderSet.isNotEmpty()) {
			val oldestItem = orderSet.first()
			val itemIsOldest = storageDurationIsNull && Clock.System.now() - oldestItem.lastUpdate > storageDuration!!
			val bufferOverflow = capacityIsNull && capacity!! < map.size
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

	@Suppress("DuplicatedCode")
	override suspend fun getAll(vararg keys: TKey): List<TValue> {
		val (localMap, remoteKeys) = mutex.withLock {
			val keysSet = keys.toSet()
			val localKeys = map.keys intersect keysSet
			val remoteKeys = keysSet - localKeys
			val localMap = localKeys.associateWith { map.getValue(it) }
			if (mode == TimedCacheMode.RelevanceMode) localMap.forEach { (key, value) -> updateOrder(key, value) }
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
				if (mode == TimedCacheMode.RelevanceMode) updateOrder(key, value)
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

