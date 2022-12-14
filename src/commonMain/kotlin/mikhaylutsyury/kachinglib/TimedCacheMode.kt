package mikhaylutsyury.kachinglib

/**
 * Перечисление, описывающее режим работы кеша.
 */
enum class TimedCacheMode {
	/**
	 * В данном режиме кеш обновляет время ключа каждый раз, когда данный ключ запрашивается.
	 */
	RelevanceMode,

	/**
	 * В данном режиме кеш обновляет время ключа только тогда, когда новая пара (ключ, значение) заносится в кеш.
	 */
	ExpirationMode,
}