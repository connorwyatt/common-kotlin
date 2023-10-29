package io.connorwyatt.common.time.clock.testing

import io.connorwyatt.common.time.clock.Clock
import java.time.Instant
import java.time.temporal.TemporalAmount

class FakeClock(private var currentInstant: Instant) : Clock {
    override fun now(): Instant = currentInstant

    fun advanceBy(temporalAmount: TemporalAmount) {
        currentInstant = currentInstant.plus(temporalAmount)
    }
}
