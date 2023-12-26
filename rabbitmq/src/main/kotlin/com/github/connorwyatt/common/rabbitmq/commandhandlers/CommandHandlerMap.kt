package com.github.connorwyatt.common.rabbitmq.commandhandlers

import com.github.connorwyatt.common.rabbitmq.Command
import kotlin.reflect.KClass

internal class CommandHandlerMap(private val definitions: Set<CommandHandlerDefinition>) {
    init {
        checkForDuplicates()
    }

    internal fun creatorFor(clazz: KClass<out com.github.connorwyatt.common.rabbitmq.Command>) =
        definitions.singleOrNull { it.clazz == clazz }?.creator
            ?: throw Exception(
                "Could not find CommandHandler creator for class (${clazz.simpleName})."
            )

    private fun checkForDuplicates() {
        if (definitions.distinct().count() != definitions.count()) {
            throw Exception("Multiple CommandHandlerMap entries registered for some command(s).")
        }
    }
}
