@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package mikhaylutsyury.kachinglib

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class TimedCacheTest : BasicCacheTest() {
	@Test
	@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
	fun testGet() = test {
		val keys = 0..10
		val data = keys.associateWith { "$it" }
		val getter: suspend (Int) -> String? = {
			delay(1)
			data[it]
		}
		val cache = TimedCache(5.milliseconds, getter = getter)
		val rawTimes = (0 until 10).map {
			measureTime {
				repeat(1000) {
					val key = Random.nextInt(keys)
					val value = getter(key)
					assertEquals("$key", value)
				}
			}
		}
		val rawTime = rawTimes.reduce { acc, duration -> acc + duration }
		val skoRawTime = (sqrt(rawTimes.map { (rawTime - it).toDouble(DurationUnit.SECONDS) }
			.sumOf { it * it }) / rawTimes.size).seconds

		val cacheTimes = (0 until 10).map {
			measureTime {
				repeat(1000) {
					val key = Random.nextInt(keys)
					val value = cache.get(key)
					assertEquals("$key", value)
				}
			}
		}
		val cacheTime = cacheTimes.reduce { acc, duration -> acc + duration }
		val skoCacheTime = (sqrt(cacheTimes.map { (cacheTime - it).toDouble(DurationUnit.SECONDS) }
			.sumOf { it * it }) / cacheTimes.size).seconds

		println("rawTime: $rawTime ± $skoRawTime")
		println("cacheTime: $cacheTime ± $skoCacheTime")
	}


	@Test
	@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
	fun testLists() = test {
		val keys = 0..10
		val data = keys.associateWith { "$it" }
		val getter: suspend (Int) -> String? = {
			delay(1)
			data[it]
		}
		val getterList: suspend (Iterable<Int>) -> Map<Int, String> = { ints ->
			delay(2)
			ints.associateWith { data.getValue(it) }
		}
		val cache = TimedCache(5.milliseconds, getterList = getterList, getter = getter)
		val rawTimes = (0 until 10).map {
			measureTime {
				repeat(1000) {
					val key = List(3) { Random.nextInt(keys) }
					val value = getterList(key)
					value.forEach { (k, v) -> assertEquals("$k", v) }
				}
			}
		}
		val rawTime = rawTimes.reduce { acc, duration -> acc + duration }
		val skoRawTime = (sqrt(rawTimes.map { (rawTime - it).toDouble(DurationUnit.SECONDS) }
			.sumOf { it * it }) / rawTimes.size).seconds

		val cacheTimes = (0 until 10).map {
			measureTime {
				repeat(1000) {
					val key = List(3) { Random.nextInt(keys) }
					val value = cache.getAll(*key.toTypedArray())
					key.indices.forEach { assertEquals("${key[it]}", value[it]) }
				}
			}
		}
		val cacheTime = cacheTimes.reduce { acc, duration -> acc + duration }
		val skoCacheTime = (sqrt(cacheTimes.map { (cacheTime - it).toDouble(DurationUnit.SECONDS) }
			.sumOf { it * it }) / cacheTimes.size).seconds

		println("rawTime: $rawTime ± $skoRawTime")
		println("cacheTime: $cacheTime ± $skoCacheTime")
	}

}