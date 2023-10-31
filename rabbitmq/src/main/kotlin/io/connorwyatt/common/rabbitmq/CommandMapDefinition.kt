package io.connorwyatt.common.rabbitmq

import kotlin.reflect.KClass

internal class CommandMapDefinition(val type: String, val clazz: KClass<out Command>)
