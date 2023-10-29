package io.connorwyatt.common.time.clock

import java.time.Instant

class RealClock : Clock {
    override fun now(): Instant = Instant.now()
}
