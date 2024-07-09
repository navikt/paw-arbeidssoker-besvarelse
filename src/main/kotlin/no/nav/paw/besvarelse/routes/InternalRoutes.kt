package no.nav.paw.besvarelse.routes

import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry

fun Route.internalRoutes(meterRegistry: PrometheusMeterRegistry) {
    route("/internal") {
        get("/isAlive") {
            call.respondText("ALIVE", ContentType.Text.Plain)
        }
        get("/isReady") {
            call.respondText("READY", ContentType.Text.Plain)
        }
        get("/metrics") {
            call.respond(meterRegistry.scrape())
        }
    }
}
