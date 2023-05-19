package no.nav.paw.besvarelse.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import no.nav.paw.besvarelse.auth.TokenService
import no.nav.paw.besvarelse.domain.request.VeilederRequest
import no.nav.paw.besvarelse.services.ArbeidssokerRegistrertService
import no.nav.paw.besvarelse.utils.getSubClaim
import no.nav.paw.besvarelse.utils.logger
import no.nav.poao_tilgang.client.NavAnsattTilgangTilEksternBrukerPolicyInput
import no.nav.poao_tilgang.client.PoaoTilgangCachedClient
import no.nav.poao_tilgang.client.PoaoTilgangClient
import no.nav.poao_tilgang.client.PoaoTilgangHttpClient
import no.nav.poao_tilgang.client.TilgangType
import org.koin.ktor.ext.inject

fun Route.veilederRoutes() {
    val client: PoaoTilgangClient = PoaoTilgangCachedClient(
        PoaoTilgangHttpClient(
            baseUrl = "http://poao-tilgang.poao.dev.svc.cluster.local", // or use "https://poao-tilgang(.dev).intern.nav.no" if your sending the request from dev-fss/prod-fss
            tokenProvider = { TokenService().createMachineToMachineToken("api://dev-gcp.poao.poao-tilgang/.default") }
        )
    )

    val arbeidssokerRegistrertService: ArbeidssokerRegistrertService by inject()

    authenticate("AAD") {
        route("api/v1/veileder") {
            post("/besvarelse") {
                logger.info("Henter besvarelse til bruker for veileder")

                val veilederId = call.getSubClaim()
                val foedselsnummer = call.receive<VeilederRequest>()

                val decision = client.evaluatePolicy(
                    NavAnsattTilgangTilEksternBrukerPolicyInput(
                        navAnsattAzureId = veilederId,
                        tilgangType = TilgangType.LESE,
                        norskIdent = foedselsnummer.toString()
                    )
                ).getOrThrow()

                val registrering = arbeidssokerRegistrertService.hentSiste(foedselsnummer)
                call.respond(HttpStatusCode.OK, registrering)
            }
        }
    }
}
