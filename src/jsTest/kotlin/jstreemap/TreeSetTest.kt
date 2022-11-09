package jstreemap

import kotlin.test.Test
import kotlin.test.assertEquals

class TreeSetTest {

	@Test
	fun commonIntTest() {
		val treeSet = JsTreeSet<Int>()
		repeat(5) { treeSet.add(it) }
		assertEquals(5, treeSet.size)
		assertEquals(0, treeSet.first())
		assertEquals(4, treeSet.last())
		val iterator = treeSet.begin()
		for (i in 0..4) {
			assertEquals(i, iterator.key)
			iterator.next()
		}
	}

	@Test
	fun commonCharTest() {
		val treeSet = JsTreeSet<Char>()
		('a'..'z').forEach { treeSet.add(it) }
		assertEquals('a', treeSet.first())
		assertEquals('z', treeSet.last())
		val iterator = treeSet.begin()
		for (i in 'a'..'z') {
			assertEquals(i, iterator.key)
			iterator.next()
		}
	}

	@Test
	fun commonStringTest() {
		val treeSet = JsTreeSet<String>()
		('a'..'z').forEach { treeSet.add("$it") }
		assertEquals("a", treeSet.first())
		assertEquals("z", treeSet.last())
		val iterator = treeSet.begin()
		for (i in 'a'..'z') {
			assertEquals("$i", iterator.key)
			iterator.next()
		}
	}


	@Test
	fun orderIntTest() {
		val treeSet = JsTreeSet<Int>()
		(4 downTo 0).forEach { treeSet.add(it) }
		assertEquals(0, treeSet.first())
		assertEquals(4, treeSet.last())
		val iterator = treeSet.begin()
		for (i in 0..4) {
			assertEquals(i, iterator.key)
			iterator.next()
		}
	}

	@Test
	fun orderCharTest() {
		val treeSet = JsTreeSet<Char>()
		('z' downTo 'a').forEach { treeSet.add(it) }
		assertEquals('a', treeSet.first())
		assertEquals('z', treeSet.last())
		val iterator = treeSet.begin()
		for (i in 'a'..'z') {
			assertEquals(i, iterator.key)
			iterator.next()
		}
	}

	@Test
	fun orderStringTest() {
		val treeSet = JsTreeSet<String>()
		('z' downTo 'a').forEach { treeSet.add("$it") }
		assertEquals("a", treeSet.first())
		assertEquals("z", treeSet.last())
		val iterator = treeSet.begin()
		for (i in 'a'..'z') {
			assertEquals("$i", iterator.key)
			iterator.next()
		}
	}


	@Test
	fun iteratorIntTest() {
		val treeSet = JsTreeSet<Int>()
		(4 downTo 0).forEach { treeSet.add(it) }
		assertEquals(0, treeSet.first())
		assertEquals(4, treeSet.last())
		var i = 0
		for (it in treeSet) {
			assertEquals(i++, it)
		}
	}

}