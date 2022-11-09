package mikhaylutsyury.kachinglib

import jstreemap.JsTreeSet
import jstreemap.iterator


actual open class TreeSet<out E : Comparable<@UnsafeVariance E>> actual constructor(vararg items: E) : Set<E> {
	protected val jsTreeSet = JsTreeSet<@UnsafeVariance E>()

	init {
		jsTreeSet.compareFunc = { lhs, rhs -> lhs.compareTo(rhs) }
		for (it in items) jsTreeSet.add(it)
	}

	override val size: Int get() = jsTreeSet.size

	override fun contains(element: @UnsafeVariance E): Boolean {
		return jsTreeSet.has(element)
	}

	override fun containsAll(elements: Collection<@UnsafeVariance E>): Boolean = elements.all { it in this }

	override fun isEmpty(): Boolean = size == 0

	override fun iterator(): Iterator<E> = jsTreeSet.iterator()
}

