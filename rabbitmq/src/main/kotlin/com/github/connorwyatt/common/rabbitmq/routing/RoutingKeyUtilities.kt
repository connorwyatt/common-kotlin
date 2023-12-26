package com.github.connorwyatt.common.rabbitmq.routing

internal object RoutingKeyUtilities {
    fun routingKeyFor(queueName: String) = "$queueName.routingKey"
}
