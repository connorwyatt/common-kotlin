package io.connorwyatt.common.rabbitmq.commandhandlers

import io.connorwyatt.common.rabbitmq.Command
import kotlin.reflect.KClass
import org.kodein.di.*

internal class CommandHandlerDefinition(
    val clazz: KClass<out Command>,
    val creator: DirectDI.() -> CommandHandler
)
