package io.connorwyatt.common.eventstore.events

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@OptIn(InternalSerializationApi::class)
object EventTransformingSerializer : JsonTransformingSerializer<Event>(Event::class.serializer()) {
    override fun transformSerialize(element: JsonElement): JsonElement =
        JsonObject(element.jsonObject.filterNot { (key) -> key == EVENT_CLASS_DISCRIMINATOR })
}
