package mikhaylutsyury.kachinglib.utils

expect interface ICloseable {
	fun close()
}

expect inline fun <T : ICloseable?, R> T.use(block: (T) -> R): R
