package io.connorwyatt.common.rabbitmq.ktor

import io.connorwyatt.common.rabbitmq.bus.CommandBus
import io.connorwyatt.common.rabbitmq.bus.InMemoryCommandBus
import io.connorwyatt.common.rabbitmq.commandhandlers.RabbitMQSubscriptionsManager
import io.connorwyatt.common.rabbitmq.configuration.RabbitMQConfiguration
import io.connorwyatt.common.rabbitmq.queues.CommandQueueCreator
import io.ktor.server.application.*
import kotlinx.coroutines.launch
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Application.configureRabbitMQ(rabbitMQConfiguration: RabbitMQConfiguration) {
    val commandQueueCreator by closestDI().instance<CommandQueueCreator>()

    launch {
        commandQueueCreator.createQueues()

        if (!rabbitMQConfiguration.useInMemoryRabbitMQ) {
            val rabbitMQSubscriptionsManager by closestDI().instance<RabbitMQSubscriptionsManager>()
            rabbitMQSubscriptionsManager.start()
        }

        val commandBus by closestDI().instance<CommandBus>()

        (commandBus as? InMemoryCommandBus)?.run { startCommandPropagation() }
    }
}
