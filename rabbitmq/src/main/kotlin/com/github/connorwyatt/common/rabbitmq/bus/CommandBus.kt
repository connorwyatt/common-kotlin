package com.github.connorwyatt.common.rabbitmq.bus

import com.github.connorwyatt.common.rabbitmq.CommandEnvelope

interface CommandBus {
    suspend fun send(commandEnvelope: com.github.connorwyatt.common.rabbitmq.CommandEnvelope)

    suspend fun send(commandEnvelopes: List<com.github.connorwyatt.common.rabbitmq.CommandEnvelope>)
}
