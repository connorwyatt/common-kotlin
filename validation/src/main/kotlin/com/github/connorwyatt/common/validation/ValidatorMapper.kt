package com.github.connorwyatt.common.validation

import com.github.connorwyatt.common.result.Result

interface ValidatorMapper<TInput, TOutput> {
    fun validateAndMap(value: TInput): Result<TOutput, List<ValidationError>>
}
