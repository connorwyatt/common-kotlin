package io.connorwyatt.common.rabbitmq.routing

import io.connorwyatt.common.rabbitmq.Command
import kotlin.reflect.KClass

internal data class CommandRoutingRules(
    private val defaultQueueName: String,
    private val rules: Map<KClass<out Command>, String>
) {
    fun queueFor(clazz: KClass<out Command>): String = rules[clazz] ?: defaultQueueName
}
