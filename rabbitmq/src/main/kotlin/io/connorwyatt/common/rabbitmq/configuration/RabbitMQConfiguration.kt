package io.connorwyatt.common.rabbitmq.configuration

data class RabbitMQConfiguration(
    val useInMemoryRabbitMQ: Boolean,
    val connectionString: String?,
    val exchangeName: String?,
)
