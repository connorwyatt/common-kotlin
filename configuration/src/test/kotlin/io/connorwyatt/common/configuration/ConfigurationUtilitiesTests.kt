package io.connorwyatt.common.configuration

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class ConfigurationUtilitiesTests {
    @Test
    fun `given a configuration file name that does not exist, when loading configuration, then it should load empty config`() {
        val configuration =
            loadConfigurationFromJsonFiles<Configuration>("nonexistent", "production")

        expectThat(configuration).isEqualTo(Configuration(null, null))
    }

    @Test
    fun `given an environment that does not exist, when loading configuration, then it should load only the base configuration`() {
        val configuration =
            loadConfigurationFromJsonFiles<Configuration>("configuration", "production")

        expectThat(configuration).isEqualTo(Configuration("world", true))
    }

    @Test
    fun `given an environment that exists, when loading configuration, then it should load both layers of configuration`() {
        val configuration = loadConfigurationFromJsonFiles<Configuration>("configuration", "test")

        expectThat(configuration).isEqualTo(Configuration("test world", true))
    }

    data class Configuration(val hello: String?, val baseOnly: Boolean?)
}
