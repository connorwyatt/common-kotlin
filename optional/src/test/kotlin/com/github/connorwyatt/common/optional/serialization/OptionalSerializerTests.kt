package com.github.connorwyatt.common.optional.serialization

import com.github.connorwyatt.common.optional.Optional
import com.github.connorwyatt.common.optional.Optional.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.*

class OptionalSerializerTests {
    @Serializable
    data class TestModel(
        val optionalString: Optional<String> = Absent,
        val optionalInt: Optional<Int> = Absent,
        val optionalBoolean: Optional<Boolean> = Absent,
        val optionalNestedModel: Optional<NestedModel> = Absent,
    ) {
        @Serializable data class NestedModel(val optionalString: Optional<String> = Absent)
    }

    @Test
    fun `given a model with present properties, when serializing, then it should serialize the properties to JSON`() {
        val testModel =
            TestModel(
                optionalString = Present("Testing"),
                optionalInt = Present(21),
                optionalBoolean = Present(true),
                optionalNestedModel =
                    Present(TestModel.NestedModel(optionalString = Present("Nested property")))
            )

        expectThat(Json.encodeToString(testModel))
            .isEqualTo(
                """{"optionalString":"Testing","optionalInt":21,"optionalBoolean":true,"optionalNestedModel":{"optionalString":"Nested property"}}"""
            )
    }

    @Test
    fun `given a model with absent properties, when serializing, then it should not serialize the properties to JSON`() {
        val testModel = TestModel()

        expectThat(Json.encodeToString(testModel)).isEqualTo("""{}""")
    }

    @Test
    fun `given a JSON string with present properties, when deserializing, then it should deserialize the properties to the model`() {
        val testModel =
            Json.decodeFromString<TestModel>(
                """{"optionalString":"Testing","optionalInt":21,"optionalBoolean":true,"optionalNestedModel":{"optionalString":"Nested property"}}"""
            )

        expectThat(testModel)
            .isEqualTo(
                TestModel(
                    optionalString = Present("Testing"),
                    optionalInt = Present(21),
                    optionalBoolean = Present(true),
                    optionalNestedModel =
                        Present(TestModel.NestedModel(optionalString = Present("Nested property")))
                )
            )
    }

    @Test
    fun `given a JSON string with absent properties, when deserializing, then it should not deserialize the properties to the model`() {
        val testModel = Json.decodeFromString<TestModel>("""{}""")

        expectThat(testModel).isEqualTo(TestModel())
    }

    @Test
    fun `given a JSON string with absent nested properties, when deserializing, then it should deserialize the properties to the model`() {
        val testModel = Json.decodeFromString<TestModel>("""{"optionalNestedModel":{}}""")

        expectThat(testModel)
            .isEqualTo(TestModel(optionalNestedModel = Present(TestModel.NestedModel())))
    }
}
