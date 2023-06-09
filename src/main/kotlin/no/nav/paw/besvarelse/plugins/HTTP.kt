package no.nav.paw.besvarelse.plugins

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.IgnoreTrailingSlash
import no.nav.paw.besvarelse.utils.logger

fun Application.configureHTTP() {
    install(IgnoreTrailingSlash)
    install(StatusPages) {
        exception<StatusException> { call, cause ->
            logger.error("Feil ved kall", cause)
            call.respond(cause.status, cause.description ?: cause.status.description)
        }
        exception<BesvarelseNotFound> { call, _ ->
            logger.info("Ingen besvarelse funnet")
            call.respond(HttpStatusCode.NoContent)
        }
    }
    install(CORS) {
        anyHost()

        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)

        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)

        allowHeadersPrefixed("nav-")
    }
}
