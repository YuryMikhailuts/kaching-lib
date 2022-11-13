package mikhaylutsyury.kachinglib

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asFlow
import mikhaylutsyury.kachinglib.utils.ICloseable

interface ICache<TKey, TValue> : ICloseable {
	/**
	 * Ёмкость кеша или `null`.
	 */
	val capacity: Int?

	/**
	 * Возвращает копию всех пар (ключ, значение), которые хранятся в кеше на данный момент.
	 * **Внимание:** вызов данного метода блокирует работу кеша.
	 */
	suspend fun cached(): Iterable<Pair<TKey, TValue>>

	/**
	 * Возвращает копию всех значений, которые хранятся в кеше на данный момент.
	 * **Внимание:** вызов данного метода блокирует работу кеша.
	 */
	suspend fun cachedValues(): Iterable<TValue>

	/**
	 * Возвращает копию всех ключей, которые хранятся в кеше на данный момент.
	 * **Внимание:** вызов данного метода блокирует работу кеша.
	 */
	suspend fun cachedKeys(): Iterable<TKey>

	/**
	 * Возвращает копию всех пар (ключ, значение), которые хранятся в кеше на данный момент.
	 * **Внимание:** вызов данного метода блокирует работу кеша.
	 */
	suspend fun cachedFlow(): Flow<Pair<TKey, TValue>> = cached().asFlow()

	/**
	 * Возвращает копию всех значений, которые хранятся в кеше на данный момент.
	 * **Внимание:** вызов данного метода блокирует работу кеша.
	 */
	suspend fun cachedValuesFlow(): Flow<TValue> = cachedValues().asFlow()

	/**
	 * Возвращает копию всех ключей, которые хранятся в кеше на данный момент.
	 * **Внимание:** вызов данного метода блокирует работу кеша.
	 */
	suspend fun cachedKeysFlow(): Flow<TKey> = cachedKeys().asFlow()

	/**
	 * В данный поток приходит каждая пара (ключ, значение), для которой было обновлено время хранения.
	 * Это может произойти в одном из следующих случаев:
	 *
	 * * Был загружен новый элемент.
	 * * Был запрошен элемент, который уже есть в кеше.
	 */
	val onUpdate: SharedFlow<Pair<TKey, TValue>>

	/**
	 * В данный поток приходит каждая пара (ключ, значение), которая была вытеснена из кеша по какой бы то ни было причине.
	 * Это может произойти в одном из следующих случаев:
	 *
	 * * Был вытеснен более новым элементом.
	 * * Был явно вызван [drop] или пришёл [FlowCollector.emit] в [doDrop].
	 */
	val onDrop: SharedFlow<Pair<TKey, TValue>>

	/**
	 * Любой ключ, пришедший в данный коллектор, будет выброшен так, как если бы просто был вызван [drop].
	 */
	val doDrop: FlowCollector<TKey>

	/**
	 * Возвращает значение по указанному ключу.
	 */
	suspend fun get(key: TKey): TValue

	/**
	 * Возвращает значения всех элементов по указанным ключам.
	 */
	suspend fun getAll(vararg keys: TKey): List<TValue>

	/**
	 * Полностью очищает кеш.
	 */
	suspend fun clear()

	/**
	 * Выбрасывает указанный элемент из кеша.
	 */
	suspend fun drop(key: TKey): TValue?

	/**
	 * Закрывает кеш. Данный метод нужен на случай, если классу, реализующему кеш будет нужно высвободить какие-либо ресурсы в конце своей работы.
	 * Желательно вызывать данный метод всякий раз, когда кеш уже точно будет не нужен.
	 */
	override fun close() = Unit
}
