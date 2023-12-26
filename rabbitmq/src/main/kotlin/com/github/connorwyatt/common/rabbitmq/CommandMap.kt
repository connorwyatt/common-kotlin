package com.github.connorwyatt.common.rabbitmq

import kotlin.reflect.KClass

internal class CommandMap(
    private val definitions: Set<com.github.connorwyatt.common.rabbitmq.CommandMapDefinition>
) {
    init {
        checkForDuplicates()
    }

    fun typeFor(clazz: KClass<out com.github.connorwyatt.common.rabbitmq.Command>) =
        definitions.singleOrNull { it.clazz == clazz }?.type
            ?: throw Exception("Could not find Command type for class (${clazz.simpleName}).")

    fun classFor(type: String) =
        definitions.singleOrNull { it.type == type }?.clazz
            ?: throw Exception("Could not find Command class for type ($type).")

    private fun checkForDuplicates() {
        if (definitions.distinct().count() != definitions.count()) {
            throw Exception("Multiple CommandMap entries registered for some type(s)/class(es).")
        }
    }
}
