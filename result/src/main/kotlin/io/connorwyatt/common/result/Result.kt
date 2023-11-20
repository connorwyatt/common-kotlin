package io.connorwyatt.common.result

sealed interface Result<out TSuccess, out TFailure> {
    fun getOrThrow(): TSuccess =
        when (this) {
            is Success -> value
            is Failure -> throw Exception("Result was a failure: $error")
        }

    data class Success<TSuccess>(val value: TSuccess) : Result<TSuccess, Nothing>

    data class Failure<TFailure>(val error: TFailure) : Result<Nothing, TFailure>
}
