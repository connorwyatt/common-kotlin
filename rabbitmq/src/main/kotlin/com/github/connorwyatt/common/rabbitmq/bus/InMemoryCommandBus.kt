package com.github.connorwyatt.common.rabbitmq.bus

import com.github.connorwyatt.common.rabbitmq.Command
import com.github.connorwyatt.common.rabbitmq.CommandEnvelope
import com.github.connorwyatt.common.rabbitmq.commandhandlers.CommandHandlerRouter
import java.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.withTimeout

class InMemoryCommandBus
internal constructor(private val commandHandlerRouter: CommandHandlerRouter) : CommandBus {
    private val commandPropagationCoroutineScope = CoroutineScope(Dispatchers.Default)
    private val commandPropagationChannel =
        Channel<com.github.connorwyatt.common.rabbitmq.Command>()

    override suspend fun send(
        commandEnvelope: com.github.connorwyatt.common.rabbitmq.CommandEnvelope
    ) {
        send(listOf(commandEnvelope))
    }

    override suspend fun send(
        commandEnvelopes: List<com.github.connorwyatt.common.rabbitmq.CommandEnvelope>
    ) {
        commandEnvelopes.forEach { commandEnvelope ->
            enqueueCommandForPropagation(commandEnvelope)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun waitForEmptyCommandPropagationQueue(timeout: Duration) {
        withTimeout(timeout) {
            while (!commandPropagationChannel.isEmpty) {
                continue
            }
        }
    }

    internal fun startCommandPropagation() {
        commandPropagationCoroutineScope.launch {
            for (command in commandPropagationChannel) {
                propagateCommandToHandler(command)
            }
        }
    }

    private suspend fun enqueueCommandForPropagation(
        commandEnvelope: com.github.connorwyatt.common.rabbitmq.CommandEnvelope
    ) {
        commandPropagationCoroutineScope.launch {
            commandPropagationChannel.send(commandEnvelope.command)
        }
    }

    private suspend fun propagateCommandToHandler(
        command: com.github.connorwyatt.common.rabbitmq.Command
    ) {
        commandHandlerRouter.handle(command)
    }
}
