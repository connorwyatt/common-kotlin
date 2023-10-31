package io.connorwyatt.common.rabbitmq.bus

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Connection
import io.connorwyatt.common.rabbitmq.Command
import io.connorwyatt.common.rabbitmq.CommandEnvelope
import io.connorwyatt.common.rabbitmq.CommandMap
import io.connorwyatt.common.rabbitmq.routing.CommandRoutingRules
import io.connorwyatt.common.rabbitmq.routing.RoutingKeyUtilities
import kotlin.reflect.KClass
import kotlinx.serialization.*
import kotlinx.serialization.json.*

internal class RabbitMQCommandBus(
    private val connection: Connection,
    private val exchangeName: String,
    private val commandMap: CommandMap,
    private val commandRoutingRules: CommandRoutingRules
) : CommandBus {
    override suspend fun send(commandEnvelope: CommandEnvelope) {
        send(listOf(commandEnvelope))
    }

    override suspend fun send(commandEnvelopes: List<CommandEnvelope>) {
        connection.createChannel().use { channel ->
            try {
                channel.txSelect()
                commandEnvelopes.forEach { commandEnvelope ->
                    // TODO: Fix this.
                    val commandClass = commandEnvelope.command::class as KClass<Command>

                    @OptIn(InternalSerializationApi::class)
                    val serializer = commandClass.serializer()

                    val serializedCommand =
                        Json.encodeToString(serializer, commandEnvelope.command).encodeToByteArray()

                    val destinationQueueName = commandRoutingRules.queueFor(commandClass)

                    channel.basicPublish(
                        exchangeName,
                        RoutingKeyUtilities.routingKeyFor("$exchangeName.$destinationQueueName"),
                        AMQP.BasicProperties.Builder()
                            .apply { type(commandMap.typeFor(commandClass)) }
                            .build(),
                        serializedCommand
                    )
                }
                channel.txCommit()
            } catch (exception: Exception) {
                channel.txRollback()
            }
        }
    }
}
