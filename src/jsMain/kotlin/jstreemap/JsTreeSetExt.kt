package jstreemap


operator fun <T : Comparable<T>> JsTreeSet<T>.iterator(): MutableIterator<T> = object : MutableIterator<T> {
	val stlIterator = begin()

	override fun hasNext(): Boolean {
		return stlIterator != end()
	}

	override fun next(): T {
		val key = stlIterator.key
		stlIterator.next()
		return key
	}

	override fun remove() {
		erase(stlIterator)
	}

}


