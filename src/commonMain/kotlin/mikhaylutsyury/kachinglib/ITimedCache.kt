package mikhaylutsyury.kachinglib

import kotlinx.datetime.Instant
import kotlin.time.Duration

interface ITimedCache<TKey, TValue> : ICache<TKey, TValue> {
	var timeout: Duration?
	fun lastUpdateTime(key: TKey): Instant?
}


