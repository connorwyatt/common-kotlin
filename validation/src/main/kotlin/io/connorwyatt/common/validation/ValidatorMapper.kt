package io.connorwyatt.common.validation

import io.connorwyatt.common.result.Result

interface ValidatorMapper<TInput, TOutput> {
    fun validateAndMap(value: TInput): Result<TOutput, List<ValidationError>>
}
