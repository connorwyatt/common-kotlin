package com.github.connorwyatt.common.rabbitmq.routing

import com.github.connorwyatt.common.rabbitmq.Command
import kotlin.reflect.KClass

internal data class CommandRoutingRules(
    private val defaultQueueName: String,
    private val rules: Map<KClass<out com.github.connorwyatt.common.rabbitmq.Command>, String>
) {
    fun queueFor(clazz: KClass<out com.github.connorwyatt.common.rabbitmq.Command>): String =
        rules[clazz] ?: defaultQueueName
}
