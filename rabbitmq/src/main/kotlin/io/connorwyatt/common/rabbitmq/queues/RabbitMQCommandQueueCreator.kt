package io.connorwyatt.common.rabbitmq.queues

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import io.connorwyatt.common.rabbitmq.routing.RoutingKeyUtilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

internal class RabbitMQCommandQueueCreator(
    private val exchangeName: String,
    private val connection: Connection,
    private val commandQueueList: CommandQueueList
) : CommandQueueCreator {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    override suspend fun createQueues() {
        createExchange(exchangeName)

        commandQueueList.definitions
            .map { definition ->
                coroutineScope.launch {
                    connection.createChannel().use { channel ->
                        createQueue(channel, exchangeName, definition.name)
                    }
                }
            }
            .joinAll()
    }

    private fun createExchange(exchangeName: String) {
        connection.createChannel().use { channel ->
            channel.exchangeDeclare(exchangeName, "direct", true)
        }
    }

    private fun createQueue(channel: Channel, exchangeName: String, queueName: String) {
        val fullQueueName = "$exchangeName.${queueName}"
        val deadLetterQueueName = "${fullQueueName}.dlq"

        val queueRoutingKey = RoutingKeyUtilities.routingKeyFor(fullQueueName)
        val deadLetterQueueRoutingKey = RoutingKeyUtilities.routingKeyFor(deadLetterQueueName)

        channel.queueDeclare(deadLetterQueueName, true, false, false, null)
        channel.queueBind(deadLetterQueueName, exchangeName, deadLetterQueueRoutingKey)
        val args =
            mapOf(
                "x-dead-letter-exchange" to exchangeName,
                "x-dead-letter-routing-key" to deadLetterQueueRoutingKey
            )
        channel.queueDeclare(fullQueueName, true, false, false, args)
        channel.queueBind(fullQueueName, exchangeName, queueRoutingKey)
    }
}
