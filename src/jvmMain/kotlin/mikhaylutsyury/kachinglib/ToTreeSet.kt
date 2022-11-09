package mikhaylutsyury.kachinglib

fun <E : Comparable<E>> java.util.TreeSet<E>.toTreeSet() = TreeSet.fromJavaTreeSet(this)