package mikhaylutsyury.kachinglib.utils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

actual interface ICloseable {
	actual fun close()
}

@Suppress("unused")
@OptIn(ExperimentalContracts::class)
actual inline fun <T : ICloseable?, R> T.use(block: (T) -> R): R {
	contract {
		callsInPlace(block, InvocationKind.EXACTLY_ONCE)
	}
	var exception: Throwable? = null
	try {
		return block(this)
	} catch (e: Throwable) {
		exception = e
		throw e
	} finally {
		this.closeFinally(exception)
	}
}

fun ICloseable?.closeFinally(cause: Throwable?) = when {
	this == null -> {}
	cause == null -> close()
	else ->
		try {
			close()
		} catch (closeException: Throwable) {
			cause.addSuppressed(closeException)
		}
}