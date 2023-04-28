package no.nav.paw.besvarelse.routes

import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.Route

fun Route.swaggerRoutes() {
    swaggerUI(path = "docs", swaggerFile = "openapi/documentation.yaml")
}
