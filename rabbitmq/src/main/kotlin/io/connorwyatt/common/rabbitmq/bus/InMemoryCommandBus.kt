package io.connorwyatt.common.rabbitmq.bus

import io.connorwyatt.common.rabbitmq.Command
import io.connorwyatt.common.rabbitmq.CommandEnvelope
import io.connorwyatt.common.rabbitmq.commandhandlers.CommandHandlerRouter
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
    private val commandPropagationChannel = Channel<Command>()

    override suspend fun send(commandEnvelope: CommandEnvelope) {
        send(listOf(commandEnvelope))
    }

    override suspend fun send(commandEnvelopes: List<CommandEnvelope>) {
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

    private suspend fun enqueueCommandForPropagation(commandEnvelope: CommandEnvelope) {
        commandPropagationCoroutineScope.launch {
            commandPropagationChannel.send(commandEnvelope.command)
        }
    }

    private suspend fun propagateCommandToHandler(command: Command) {
        commandHandlerRouter.handle(command)
    }
}
