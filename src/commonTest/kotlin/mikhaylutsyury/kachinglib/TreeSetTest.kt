package mikhaylutsyury.kachinglib

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TreeSetTest {
	@Test
	fun iteratorTest() {
		val treeSet = TreeSet(1, 2, 3, 4, 5)
		var j = 1
		for (i in treeSet) {
			assertEquals(j++, i)
		}
	}

	@Test
	fun sizeTest() {
		val treeSet = TreeSet(1, 2, 3, 4, 5)
		assertEquals(5, treeSet.size)
	}

	@Test
	fun isEmptyTest() {
		val treeSet5 = TreeSet(1, 2, 3, 4, 5)
		assertFalse { treeSet5.isEmpty() }
		val treeSet0 = TreeSet<Int>()
		assertTrue { treeSet0.isEmpty() }
	}

	@Test
	fun orderIntTest() {
		val treeSet = TreeSet(5, 2, 3, 4, 1)
		var j = 1
		for (i in treeSet) {
			assertEquals(j++, i)
		}
	}

	@Test
	fun orderCustomClassTest() {
		data class TestItem(val v: Int) : Comparable<TestItem> {
			override fun compareTo(other: TestItem): Int = v.compareTo(other.v)
		}

		val treeSet = TreeSet(*(5 downTo 1).map { TestItem(it) }.toTypedArray())
		var j = 1
		for (i in treeSet) {
			assertEquals(TestItem(j++), i)
		}
	}

	@Test
	fun containsTest() {
		val treeSet = TreeSet(5, 4, 3, 2, 1)
		println("contains test")
		for (it in 1..5) {
			assertTrue("value '$it'. ") { it in treeSet }
		}
		println("not contains test 1")
		for (it in 6..100) {
			assertFalse("value '$it'. ") { it in treeSet }
		}
		println("not contains test 2")
		for (it in -100..0) {
			assertFalse("value '$it'. ") { it in treeSet }
		}
	}
}