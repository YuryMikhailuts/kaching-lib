package mikhaylutsyury.kachinglib

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import kotlin.time.Duration

/**
 * Кеш на базе буфера конечного или бесконечного размера с возможностью выбрасывать элементы, которые не запрашивались дольше определённого времени.
 * При [storageDuration] равном `null` ведёт себя точно так же, как и [BufferedCache].
 * @param storageDuration время, в течение которого элемент хранится, если его никто не запрашивает. Элементы, которые хранятся дольше, чем [storageDuration] могу быть вытеснены более новыми.
 * @param capacity размер буфера, если он конечен, или `null`, что бы создать кеш с буфером бесконечного размера.
 * @param dispatcher диспетчер корутин ([scope] : [CoroutineScope]) для отправки событий [onDrop] и [onUpdate]. По умолчанию равен [Dispatchers.Default].
 * @param getter `suspend` функция, которая должна будет возвращать оригинальные значения по ключу, если они отсутствуют в кеше.
 */
class TimedCache<TKey, TValue : Any?>(
	var storageDuration: Duration? = null,
	override var capacity: Int? = null,
	dispatcher: CoroutineDispatcher = Dispatchers.Default,
	getter: suspend (TKey) -> TValue,
) : BufferedCache<TKey, TValue>(capacity, dispatcher, getter), ICache<TKey, TValue> {
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

}