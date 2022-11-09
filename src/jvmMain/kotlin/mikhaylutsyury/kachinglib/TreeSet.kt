package mikhaylutsyury.kachinglib

import java.util.TreeSet as JTreeSet

actual open class TreeSet<out E : Comparable<@UnsafeVariance E>> private constructor(
	protected open val treeSet: JTreeSet<@UnsafeVariance E>,
) : Set<E> by treeSet {
	companion object {
		fun <E : Comparable<E>> fromJavaTreeSet(treeSet: JTreeSet<E>) = TreeSet(treeSet)
	}

	actual constructor(vararg items: E) : this(JTreeSet(items.toList()))
}


