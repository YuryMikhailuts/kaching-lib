@file:JsModule("jstreemap/jstreemap.js")
@file:JsNonModule

package jstreemap

@JsName("TreeSet")
external class JsTreeSet<T : Comparable<T>> {
	fun add(v: T)
	fun begin(): StlIterator<T, Unit>
	fun end(): StlIterator<T, Unit>
	fun rbegin(): StlReverseIterator<T, Unit>
	fun rend(): StlReverseIterator<T, Unit>
	fun first(): T?
	fun last(): T?
	fun clear()
	fun delete(key: T)
	fun erase(iterator: StlIterator<T, Unit>)
	fun has(key: T): Boolean
	val size: Int
	var compareFunc: ((lhs: T, rhs: T) -> Int)?
}

@JsName("BaseIterator")
external interface StlBaseIterator<K, V> {
	fun next()
	fun prev()
	val key: K
	val value: V
}

@JsName("Iterator")
external interface StlIterator<K, V> : StlBaseIterator<K, V>

@JsName("ReverseIterator")
external interface StlReverseIterator<K, V> : StlBaseIterator<K, V>

external interface JsIterator {
	fun next(): dynamic
	fun backwards(): JsReverseIterator
}

external interface JsReverseIterator {
	fun next(): dynamic
	fun backwards(): JsIterator
}

