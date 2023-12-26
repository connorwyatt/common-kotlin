package com.github.connorwyatt.common.rabbitmq.commandhandlers

import com.github.connorwyatt.common.rabbitmq.Command
import kotlin.reflect.KClass

internal class CommandHandlerRouter(
    private val factory:
        (KClass<out com.github.connorwyatt.common.rabbitmq.Command>) -> CommandHandler
) {
    internal suspend fun handle(command: com.github.connorwyatt.common.rabbitmq.Command) {
        val commandClass = command::class
        val commandHandler = factory(commandClass)
        commandHandler.handleCommand(command)
    }
}
