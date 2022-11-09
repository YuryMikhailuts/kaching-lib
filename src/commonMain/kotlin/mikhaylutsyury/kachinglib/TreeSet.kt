package mikhaylutsyury.kachinglib

expect open class TreeSet<out E : Comparable<@UnsafeVariance E>>(vararg items: E) : Set<E>

