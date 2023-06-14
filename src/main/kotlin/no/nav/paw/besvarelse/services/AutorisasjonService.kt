package no.nav.paw.besvarelse.services

import no.nav.paw.besvarelse.domain.Foedselsnummer
import no.nav.paw.besvarelse.domain.NavAnsatt
import no.nav.paw.besvarelse.utils.auditLogMelding
import no.nav.paw.besvarelse.utils.autitLogger
import no.nav.paw.besvarelse.utils.logger
import no.nav.poao_tilgang.client.NavAnsattTilgangTilEksternBrukerPolicyInput
import no.nav.poao_tilgang.client.PoaoTilgangCachedClient
import no.nav.poao_tilgang.client.TilgangType

class AutorisasjonService(
    val poaoTilgangHttpClient: PoaoTilgangCachedClient
) {
    fun verifiserVeilederTilgangTilBruker(navAnsatt: NavAnsatt, foedselsnummer: Foedselsnummer): Boolean {
        logger.info("Henter arbeidssøkerperioder for veileder: '${navAnsatt.ident}' bruker: $foedselsnummer")
        autitLogger.info(
            auditLogMelding(
                foedselsnummer,
                navAnsatt,
                "Veileder ${navAnsatt.ident} forsøker å lese besvarelse til arbeidssøker=${foedselsnummer.foedselsnummer}"
            )
        )

        val harNavAnsattTilgang = poaoTilgangHttpClient.evaluatePolicy(
            NavAnsattTilgangTilEksternBrukerPolicyInput(
                navAnsatt.azureId,
                TilgangType.LESE,
                foedselsnummer.foedselsnummer
            )
        ).getOrThrow()
            .isPermit

        if (!harNavAnsattTilgang) {
            logger.warn(
                "NAV-ansatt med ident: '${navAnsatt.ident}' har ikke tilgang til bruker"
            )
        }

        return harNavAnsattTilgang
    }
}
