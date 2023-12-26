package com.github.connorwyatt.common.result

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo

class ResultTests {
    @Test
    fun `getOrThrow returns the value when Result is a success`() {
        val value = "value"

        val result = Result.Success(value)

        expectThat(result.getOrThrow()).isEqualTo(value)
    }

    @Test
    fun `getOrThrow throws when Result is a failure`() {
        val value = "value"

        val result = Result.Failure(value)

        expectThrows<Exception> { result.getOrThrow() }
    }
}
