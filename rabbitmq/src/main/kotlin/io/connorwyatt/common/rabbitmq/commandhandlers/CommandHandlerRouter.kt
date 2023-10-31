package io.connorwyatt.common.rabbitmq.commandhandlers

import io.connorwyatt.common.rabbitmq.Command
import kotlin.reflect.KClass

internal class CommandHandlerRouter(private val factory: (KClass<out Command>) -> CommandHandler) {
    internal suspend fun handle(command: Command) {
        val commandClass = command::class
        val commandHandler = factory(commandClass)
        commandHandler.handleCommand(command)
    }
}
