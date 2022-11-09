package mikhaylutsyury.kachinglib

import jstreemap.iterator

actual class MutableTreeSet<E : Comparable<E>>
actual constructor(vararg items: E) : TreeSet<E>(), MutableSet<E> {
	override fun add(element: E): Boolean = (element !in this).also { jsTreeSet.add(element) }

	override fun addAll(elements: Collection<E>): Boolean = elements.all { add(it) }

	override fun clear() = jsTreeSet.clear()

	actual override fun iterator(): MutableIterator<E> = jsTreeSet.iterator()

	override fun retainAll(elements: Collection<E>): Boolean = removeAll(filter { it !in elements })

	override fun removeAll(elements: Collection<E>): Boolean = elements.all { remove(it) }

	override fun remove(element: E): Boolean = (element in this).also { jsTreeSet.delete(element) }

}