package io.connorwyatt.common.rabbitmq.queues

internal interface CommandQueueCreator {
    suspend fun createQueues()
}
