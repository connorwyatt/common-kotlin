package io.connorwyatt.common.rabbitmq.kodein

import io.connorwyatt.common.rabbitmq.Command
import io.connorwyatt.common.rabbitmq.CommandMapDefinition
import io.connorwyatt.common.rabbitmq.commandhandlers.CommandHandler
import io.connorwyatt.common.rabbitmq.commandhandlers.CommandHandlerDefinition
import io.connorwyatt.common.rabbitmq.queues.CommandQueueDefinition
import io.connorwyatt.common.rabbitmq.routing.CommandRoutingRulesBuilder
import kotlin.reflect.KClass
import org.kodein.di.*

fun DI.Builder.bindCommandQueueDefinition(queueName: String) {
    inBindSet<CommandQueueDefinition> { add { singleton { CommandQueueDefinition(queueName) } } }
}

fun <TCommand : Command> DI.Builder.bindCommandDefinition(type: String, clazz: KClass<TCommand>) {
    inBindSet<CommandMapDefinition> { add { singleton { CommandMapDefinition(type, clazz) } } }
}

inline fun <reified TCommand : Command> DI.Builder.bindCommandDefinition(type: String) {
    bindCommandDefinition(type, TCommand::class)
}

fun <TCommand : Command> DI.Builder.bindCommandHandler(
    constructor: DirectDI.() -> CommandHandler,
    clazz: KClass<TCommand>
) {
    inBindSet<CommandHandlerDefinition> {
        add { singleton { CommandHandlerDefinition(clazz, constructor) } }
    }
}

inline fun <reified TCommand : Command> DI.Builder.bindCommandHandler(
    noinline constructor: DirectDI.() -> CommandHandler
) {
    bindCommandHandler(constructor, TCommand::class)
}

fun DI.Builder.bindCommandRoutingRules(function: CommandRoutingRulesBuilder.() -> Unit) {
    val builder = CommandRoutingRulesBuilder()

    function(builder)

    bindSingleton { builder.build() }
}
