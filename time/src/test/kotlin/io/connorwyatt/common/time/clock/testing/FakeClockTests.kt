package io.connorwyatt.common.time.clock.testing

import io.connorwyatt.common.time.TimeUtilities
import java.time.Duration
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class FakeClockTests {
    @Test
    fun `should return the instant provided`() {
        val instant = TimeUtilities.instantOf(2023, 1, 1, 12, 0, 0, 0)

        val fakeClock = FakeClock(instant)

        expectThat(fakeClock.now()).isEqualTo(instant)
    }

    @Test
    fun `should be able to be advanced`() {
        val instant = TimeUtilities.instantOf(2023, 1, 1, 12, 0, 0, 0)

        val fakeClock = FakeClock(instant)

        fakeClock.advanceBy(Duration.ofHours(1))

        expectThat(fakeClock.now()).isEqualTo(instant.plus(Duration.ofHours(1)))
    }
}
