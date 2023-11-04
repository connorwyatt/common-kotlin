package io.connorwyatt.common.server

import io.ktor.server.cio.*
import io.ktor.server.engine.*

class Server(port: Int, private val applicationConfiguration: ApplicationConfiguration) {
    private val applicationEngine =
        embeddedServer(CIO, port = port, host = "localhost") {
            applicationConfiguration.applyTo(this)
        }

    fun start() {
        applicationEngine.start(wait = true)
    }
}
