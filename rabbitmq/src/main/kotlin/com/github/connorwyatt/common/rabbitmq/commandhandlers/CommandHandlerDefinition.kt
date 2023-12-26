package com.github.connorwyatt.common.rabbitmq.commandhandlers

import com.github.connorwyatt.common.rabbitmq.Command
import kotlin.reflect.KClass
import org.kodein.di.*

internal class CommandHandlerDefinition(
    val clazz: KClass<out com.github.connorwyatt.common.rabbitmq.Command>,
    val creator: DirectDI.() -> CommandHandler
)
