package com.github.connorwyatt.common.rabbitmq.commandhandlers

import com.github.connorwyatt.common.rabbitmq.Command
import kotlin.reflect.KClass

abstract class CommandHandler {
    private var handlers =
        mapOf<
            KClass<out com.github.connorwyatt.common.rabbitmq.Command>,
            suspend (com.github.connorwyatt.common.rabbitmq.Command) -> Unit
        >()

    protected fun <TCommand : com.github.connorwyatt.common.rabbitmq.Command> handle(
        commandClass: KClass<TCommand>,
        handler: suspend (TCommand) -> Unit
    ) {
        @Suppress("UNCHECKED_CAST")
        handlers =
            handlers.plus(
                commandClass as KClass<out com.github.connorwyatt.common.rabbitmq.Command> to
                    handler as suspend (com.github.connorwyatt.common.rabbitmq.Command) -> Unit
            )
    }

    protected inline fun <reified TCommand : com.github.connorwyatt.common.rabbitmq.Command> handle(
        noinline handler: suspend (TCommand) -> Unit
    ) {
        handle(TCommand::class, handler)
    }

    internal suspend fun handleCommand(
        command: com.github.connorwyatt.common.rabbitmq.Command,
    ) = getHandlerOrThrow(command).invoke(command)

    private fun getHandlerOrThrow(command: com.github.connorwyatt.common.rabbitmq.Command) =
        (handlers[command::class]
            ?: throw Exception(
                "CommandHandler has no handler for command ${command::class.simpleName}"
            ))
}
