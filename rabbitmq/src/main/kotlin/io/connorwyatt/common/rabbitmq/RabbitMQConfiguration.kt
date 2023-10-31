package io.connorwyatt.common.rabbitmq

data class RabbitMQConfiguration(
    val useInMemoryRabbitMQ: Boolean,
    val connectionString: String?,
    val exchangeName: String?,
)
