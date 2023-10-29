package io.connorwyatt.common.eventstore.aggregates

import kotlin.reflect.KClass

class AggregateMap(private val definitions: Set<AggregateMapDefinition<Aggregate>>) {
    init {
        checkForDuplicates()
    }

    @Suppress("UNCHECKED_CAST")
    internal fun <TAggregate : Aggregate> definitionFor(
        clazz: KClass<TAggregate>
    ): AggregateMapDefinition<TAggregate> =
        definitions.single { it.clazz == clazz } as AggregateMapDefinition<TAggregate>

    internal inline fun <reified TAggregate : Aggregate> definitionFor():
        AggregateMapDefinition<TAggregate> = definitionFor(TAggregate::class)

    private fun checkForDuplicates() {
        if (definitions.distinctBy { it.category }.count() != definitions.count()) {
            throw Exception("Multiple AggregateMapDefinitions defined for some category(s).")
        }
    }
}
