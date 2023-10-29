package io.connorwyatt.common.time

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

object TimeUtilities {
    val utcZone = ZoneId.of("UTC") ?: throw Exception("Could not get UTC zone.")

    fun instantOf(
        year: Int,
        month: Int,
        dayOfMonth: Int,
        hour: Int = 0,
        minute: Int = 0,
        second: Int = 0,
        nanoOfSecond: Int = 0,
    ): Instant =
        ZonedDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond, utcZone)
            .toInstant()
}
