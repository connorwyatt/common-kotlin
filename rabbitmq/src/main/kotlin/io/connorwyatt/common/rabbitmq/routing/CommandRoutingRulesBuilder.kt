package io.connorwyatt.common.rabbitmq.routing

import io.connorwyatt.common.rabbitmq.Command
import kotlin.reflect.KClass

class CommandRoutingRulesBuilder internal constructor() {
    private var defaultQueueName: String? = null
    private var rules = emptyMap<KClass<out Command>, String>()

    fun defaultQueue(queueName: String) {
        if (defaultQueueName != null) {
            throw Exception("Default queue already set.")
        }

        defaultQueueName = queueName
    }

    fun queueFor(clazz: KClass<out Command>, queueName: String) {
        if (rules.containsKey(clazz)) {
            throw Exception("Already registered queue for ${clazz.simpleName}.")
        }

        rules = rules.plus(clazz to queueName)
    }

    inline fun <reified TCommand : Command> queueFor(queueName: String) {
        queueFor(TCommand::class, queueName)
    }

    internal fun build(): CommandRoutingRules {
        return defaultQueueName?.let { CommandRoutingRules(it, rules) }
            ?: throw Exception("Default queue not set.")
    }
}
