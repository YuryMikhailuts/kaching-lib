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
	fun test() = test {
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
}