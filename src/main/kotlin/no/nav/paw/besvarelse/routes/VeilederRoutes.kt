package no.nav.paw.besvarelse.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import no.nav.paw.besvarelse.domain.Foedselsnummer
import no.nav.paw.besvarelse.domain.request.VeilederRequest
import no.nav.paw.besvarelse.services.ArbeidssokerRegistrertService
import no.nav.paw.besvarelse.services.AutorisasjonService
import no.nav.paw.besvarelse.utils.getNavAnsatt
import no.nav.paw.besvarelse.utils.logger
import org.koin.ktor.ext.inject

fun Route.veilederRoutes() {
    val arbeidssokerRegistrertService: ArbeidssokerRegistrertService by inject()
    val autorisasjonService: AutorisasjonService by inject()

    authenticate("azure") {
        route("api/v1/veileder") {
            post("/har-tilgang") {
                logger.info("Sjekker om NAV-ansatt har tilgang til bruker")

                val navAnsatt = call.getNavAnsatt()
                val foedselsnummer = Foedselsnummer(call.receive<VeilederRequest>().foedselsnummer)
                val harNavBrukerTilgang =
                    autorisasjonService.verifiserVeilederTilgangTilBruker(navAnsatt, foedselsnummer)

                if (!harNavBrukerTilgang) {
                    return@post call.respond(HttpStatusCode.Forbidden, "NAV-ansatt har ikke tilgang")
                }

                call.respond(HttpStatusCode.OK, "NAV-ansatt har tilgang")
            }

            post("/besvarelse") {
                logger.info("Henter besvarelse til bruker til veileder")

                val navAnsatt = call.getNavAnsatt()
                val foedselsnummer = Foedselsnummer(call.receive<VeilederRequest>().foedselsnummer)

                val harNavBrukerTilgang =
                    autorisasjonService.verifiserVeilederTilgangTilBruker(navAnsatt, foedselsnummer)

                if (!harNavBrukerTilgang) {
                    return@post call.respond(HttpStatusCode.Forbidden, "NAV-ansatt har ikke tilgang")
                }

                val registrering = arbeidssokerRegistrertService.hentSiste(foedselsnummer)

                call.respond(HttpStatusCode.OK, registrering)
            }
        }
    }
}
