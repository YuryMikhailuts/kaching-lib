package mikhaylutsyury.kachinglib

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.FlowCollector

abstract class BasicCache<TKey, TValue>(private val dispatcher: CoroutineDispatcher) : ICache<TKey, TValue> {
	protected open val context by lazy { dispatcher + SupervisorJob() }
	protected val scope by lazy { CoroutineScope(context) }
	override val doDrop: FlowCollector<TKey> = FlowCollector { drop(it) }
	override fun close() {
		scope.cancel("This cache is closed.")
	}
}