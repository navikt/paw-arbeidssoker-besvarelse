package no.nav.paw.besvarelse.routes

import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.response.*
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat

private val collectorRegistry: CollectorRegistry = CollectorRegistry.defaultRegistry

fun Route.internalRoutes(appMicrometerRegistry: PrometheusMeterRegistry) {
    route("/internal") {
        get("/isAlive") {
            call.respondText("ALIVE", ContentType.Text.Plain)
        }
        get("/isReady") {
            call.respondText("READY", ContentType.Text.Plain)
        }
        get("/metrics") {
            call.respond(appMicrometerRegistry.scrape())
        }
    }
}
