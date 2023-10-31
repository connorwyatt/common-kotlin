package io.connorwyatt.common.rabbitmq

import com.rabbitmq.client.ConnectionFactory
import io.connorwyatt.common.rabbitmq.bus.CommandBus
import io.connorwyatt.common.rabbitmq.bus.InMemoryCommandBus
import io.connorwyatt.common.rabbitmq.bus.RabbitMQCommandBus
import io.connorwyatt.common.rabbitmq.commandhandlers.CommandHandlerDefinition
import io.connorwyatt.common.rabbitmq.commandhandlers.CommandHandlerMap
import io.connorwyatt.common.rabbitmq.commandhandlers.CommandHandlerRouter
import io.connorwyatt.common.rabbitmq.commandhandlers.RabbitMQSubscriptionsManager
import io.connorwyatt.common.rabbitmq.queues.CommandQueueCreator
import io.connorwyatt.common.rabbitmq.queues.CommandQueueDefinition
import io.connorwyatt.common.rabbitmq.queues.CommandQueueList
import io.connorwyatt.common.rabbitmq.queues.NoopCommandQueueCreator
import io.connorwyatt.common.rabbitmq.queues.RabbitMQCommandQueueCreator
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
