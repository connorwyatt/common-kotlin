package io.connorwyatt.common.rabbitmq.commandhandlers

import io.connorwyatt.common.rabbitmq.Command
import kotlin.reflect.KClass

abstract class CommandHandler {
    private var handlers = mapOf<KClass<out Command>, suspend (Command) -> Unit>()

    protected fun <TCommand : Command> handle(
        commandClass: KClass<TCommand>,
        handler: suspend (TCommand) -> Unit
    ) {
        @Suppress("UNCHECKED_CAST")
        handlers =
            handlers.plus(
                commandClass as KClass<out Command> to handler as suspend (Command) -> Unit
            )
    }

    protected inline fun <reified TCommand : Command> handle(
        noinline handler: suspend (TCommand) -> Unit
    ) {
        handle(TCommand::class, handler)
    }

    internal suspend fun handleCommand(
        command: Command,
    ) = getHandlerOrThrow(command).invoke(command)

    private fun getHandlerOrThrow(command: Command) =
        (handlers[command::class]
            ?: throw Exception(
                "CommandHandler has no handler for command ${command::class.simpleName}"
            ))
}
