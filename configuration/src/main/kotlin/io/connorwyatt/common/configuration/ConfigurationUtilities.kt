package io.connorwyatt.common.configuration

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addEnvironmentSource
import com.sksamuel.hoplite.addResourceSource

/**
 * Loads configuration from JSON files based on the specified environment.
 *
 * Looks for resources named `{configurationFileName}.json` and
 * `{configurationFileName}.{environment}.json` as well as environment variables.
 *
 * @param <T> The type of the configuration object to return.
 * @param configurationFileName The base name of the configuration files.
 * @param environment The environment for which the configuration should be loaded.
 * @return The configuration object of type T.
 */
inline fun <reified T : Any> loadConfigurationFromJsonFiles(
    configurationFileName: String,
    environment: String
): T =
    ConfigLoaderBuilder.default()
        .apply {
            addEnvironmentSource()
            addResourceSource("/$configurationFileName.$environment.json", optional = true)
            addResourceSource("/$configurationFileName.json", optional = true)
        }
        .build()
        .loadConfigOrThrow<T>()
