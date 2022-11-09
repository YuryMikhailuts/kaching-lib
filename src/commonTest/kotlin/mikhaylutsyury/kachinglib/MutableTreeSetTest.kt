package mikhaylutsyury.kachinglib

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MutableTreeSetTest {
	@Test
	fun addTest() {
		val treeSet = MutableTreeSet<Int>()
		repeat(50) {
			assertTrue { it !in treeSet }
			treeSet.add(it)
			assertTrue { it in treeSet }
		}
	}

	@Test
	fun removeTest() {
		val treeSet = MutableTreeSet<Int>()
		repeat(50) {
			treeSet.add(it)
		}
		repeat(50) {
			assertTrue { it in treeSet }
			treeSet.remove(it)
			assertTrue { it !in treeSet }
		}
	}

	@Test
	fun addAllTest() {
		val treeSet = MutableTreeSet<Int>()
		val range = (1..50).toList()
		assertFalse { treeSet.containsAll(range) }
		treeSet.addAll(range)
		assertTrue { treeSet.containsAll(range) }
	}

	@Test
	fun removeAllTest() {
		val treeSet = MutableTreeSet<Int>()
		val range = (1..50).toList()
		treeSet.addAll(range)
		assertTrue { treeSet.containsAll(range) }
		treeSet.removeAll(range)
		assertFalse { treeSet.containsAll(range) }
	}

	@Test
	fun retainAllTest() {
		val treeSet = MutableTreeSet<Int>()
		val range1 = (1..50).toList()
		val range2 = (51..100).toList()
		treeSet.addAll(range1)
		treeSet.addAll(range2)
		assertTrue { treeSet.containsAll(range1) }
		assertTrue { treeSet.containsAll(range2) }
		treeSet.retainAll(range1)
		assertTrue { treeSet.containsAll(range1) }
		assertFalse { treeSet.containsAll(range2) }
		treeSet.addAll(range2)
		treeSet.retainAll(range2)
		assertFalse { treeSet.containsAll(range1) }
		assertTrue { treeSet.containsAll(range2) }
	}

	@Test
	fun clearTest() {
		val treeSet = MutableTreeSet<Int>()
		val range = (1..50).toList()
		assertTrue { treeSet.isEmpty() }
		treeSet.addAll(range)
		assertTrue { treeSet.isNotEmpty() }
		treeSet.clear()
		assertTrue { treeSet.isEmpty() }
	}

}