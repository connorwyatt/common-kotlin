package com.github.connorwyatt.common.rabbitmq.kodein

import com.github.connorwyatt.common.rabbitmq.CommandMap
import com.github.connorwyatt.common.rabbitmq.CommandMapDefinition
import com.github.connorwyatt.common.rabbitmq.bus.CommandBus
import com.github.connorwyatt.common.rabbitmq.bus.InMemoryCommandBus
import com.github.connorwyatt.common.rabbitmq.bus.RabbitMQCommandBus
import com.github.connorwyatt.common.rabbitmq.commandhandlers.CommandHandlerDefinition
import com.github.connorwyatt.common.rabbitmq.commandhandlers.CommandHandlerMap
import com.github.connorwyatt.common.rabbitmq.commandhandlers.CommandHandlerRouter
import com.github.connorwyatt.common.rabbitmq.commandhandlers.RabbitMQSubscriptionsManager
import com.github.connorwyatt.common.rabbitmq.configuration.RabbitMQConfiguration
import com.github.connorwyatt.common.rabbitmq.queues.CommandQueueCreator
import com.github.connorwyatt.common.rabbitmq.queues.CommandQueueDefinition
import com.github.connorwyatt.common.rabbitmq.queues.CommandQueueList
import com.github.connorwyatt.common.rabbitmq.queues.NoopCommandQueueCreator
import com.github.connorwyatt.common.rabbitmq.queues.RabbitMQCommandQueueCreator
import com.rabbitmq.client.ConnectionFactory
import org.kodein.di.*

fun rabbitMQDependenciesModule(rabbitMQConfiguration: RabbitMQConfiguration): DI.Module =
    DI.Module(name = ::rabbitMQDependenciesModule.name) {
        if (!rabbitMQConfiguration.useInMemoryRabbitMQ) {
            val connectionString =
                (rabbitMQConfiguration.connectionString
                    ?: throw Exception("Connection string not set for RabbitMQ."))

            val exchangeName =
                (rabbitMQConfiguration.exchangeName
                    ?: throw Exception("Exchange name not set for RabbitMQ."))

            bindSingleton { ConnectionFactory().apply { setUri(connectionString) } }
            bindSingleton { instance<ConnectionFactory>().newConnection() }

            bindProvider<CommandQueueCreator> {
                RabbitMQCommandQueueCreator(exchangeName, instance(), instance())
            }

            bindProvider<CommandBus> {
                RabbitMQCommandBus(instance(), exchangeName, instance(), instance())
            }

            bindSingleton {
                RabbitMQSubscriptionsManager(
                    rabbitMQConfiguration.exchangeName,
                    instance(),
                    instance(),
                    instance(),
                    instance(),
                )
            }
        } else {
            bindProvider<CommandQueueCreator> { new(::NoopCommandQueueCreator) }
            bindSingleton<CommandBus> { new(::InMemoryCommandBus) }
        }

        bindSingletonOf(::CommandQueueList)
        bindSet<CommandQueueDefinition>()

        bindSingletonOf(::CommandMap)
        bindSet<CommandMapDefinition>()

        bindSingletonOf(::CommandHandlerMap)
        bindSet<CommandHandlerDefinition>()

        bindSingleton {
            CommandHandlerRouter { clazz ->
                val creator = instance<CommandHandlerMap>().creatorFor(clazz)

                newInstance { creator(this) }
            }
        }
    }
