package com.github.connorwyatt.common.rabbitmq

import kotlin.reflect.KClass

internal class CommandMapDefinition(
    val type: String,
    val clazz: KClass<out com.github.connorwyatt.common.rabbitmq.Command>
)
