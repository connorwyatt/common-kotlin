package io.connorwyatt.common.rabbitmq.bus

import io.connorwyatt.common.rabbitmq.CommandEnvelope

interface CommandBus {
    suspend fun send(commandEnvelope: CommandEnvelope)

    suspend fun send(commandEnvelopes: List<CommandEnvelope>)
}
