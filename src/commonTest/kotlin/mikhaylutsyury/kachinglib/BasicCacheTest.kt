@file:OptIn(ExperimentalCoroutinesApi::class)
@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package mikhaylutsyury.kachinglib

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test

abstract class BasicCacheTest {
	protected val scope = CoroutineScope(Dispatchers.Unconfined)

	protected fun test(callback: suspend () -> Unit): TestResult {
		return runTest(dispatchTimeoutMs = Long.MAX_VALUE) { scope.launch(EmptyCoroutineContext) { callback() }.join() }
	}
}