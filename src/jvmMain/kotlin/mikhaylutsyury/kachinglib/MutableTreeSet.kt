package mikhaylutsyury.kachinglib

actual class MutableTreeSet<E : Comparable<E>> private constructor(
	override val treeSet: java.util.TreeSet<E>,
) : TreeSet<E>(), MutableSet<E> by treeSet {
	actual constructor(vararg items: E) : this(java.util.TreeSet(items.toList()))

	override fun contains(element: E): Boolean = treeSet.contains(element)

	override fun containsAll(elements: Collection<E>): Boolean = treeSet.containsAll(elements)

	override fun isEmpty(): Boolean = treeSet.isEmpty()

	actual override fun iterator(): MutableIterator<E> = treeSet.iterator()

	override val size: Int get() = treeSet.size
}