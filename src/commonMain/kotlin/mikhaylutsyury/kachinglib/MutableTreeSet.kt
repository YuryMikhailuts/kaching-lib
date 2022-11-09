package mikhaylutsyury.kachinglib

expect class MutableTreeSet<E : Comparable<E>>(vararg items: E) : TreeSet<E>, MutableSet<E> {
	override fun iterator(): MutableIterator<E>
}