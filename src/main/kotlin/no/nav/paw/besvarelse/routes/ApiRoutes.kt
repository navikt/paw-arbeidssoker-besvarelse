package no.nav.paw.besvarelse.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.route
import no.nav.paw.besvarelse.domain.besvarelse.EndretAv
import no.nav.paw.besvarelse.domain.request.EndreBesvarelseRequest
import no.nav.paw.besvarelse.services.ArbeidssokerRegistrertService
import no.nav.paw.besvarelse.utils.getPidClaim
import no.nav.paw.besvarelse.utils.logger
import org.koin.ktor.ext.inject

fun Route.apiRoutes() {
    val arbeidssokerRegistrertService: ArbeidssokerRegistrertService by inject()

    authenticate("tokendings", "idporten") {
        route("/api/v1") {
            get("/besvarelse") {
                logger.info("Henter siste besvarelse til bruker")

                val foedselsnummer = call.getPidClaim()

                val registrering = arbeidssokerRegistrertService.hentSiste(foedselsnummer)

                call.respond(HttpStatusCode.OK, registrering)
            }

            patch("/besvarelse/situasjon") {
                logger.info("Endrer situasjon i besvarelse til bruker")

                val endretSituasjon = call.receive<EndreBesvarelseRequest>()
                val foedselsnummer = call.getPidClaim()

                val arbeidssokerRegistrertResponse = arbeidssokerRegistrertService.endreSituasjon(
                    foedselsnummer,
                    endretSituasjon,
                    EndretAv.BRUKER
                )

                call.respond(HttpStatusCode.OK, arbeidssokerRegistrertResponse)
            }
        }
    }
}
