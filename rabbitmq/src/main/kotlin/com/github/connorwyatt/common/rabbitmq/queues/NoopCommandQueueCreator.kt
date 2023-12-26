package com.github.connorwyatt.common.rabbitmq.queues

internal class NoopCommandQueueCreator : CommandQueueCreator {
    override suspend fun createQueues() {}
}
