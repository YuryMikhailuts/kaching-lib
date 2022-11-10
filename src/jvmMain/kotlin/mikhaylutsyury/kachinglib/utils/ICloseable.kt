package mikhaylutsyury.kachinglib.utils

import kotlin.use as jUse

actual typealias ICloseable = AutoCloseable

actual inline fun <T : ICloseable?, R> T.use(block: (T) -> R): R = jUse(block)