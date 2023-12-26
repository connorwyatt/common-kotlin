package com.github.connorwyatt.common.time.clock

import java.time.Instant

interface Clock {
    fun now(): Instant
}
