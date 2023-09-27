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
    private val poaoTilgangHttpClient: PoaoTilgangCachedClient
) {
    fun verifiserVeilederTilgangTilBruker(navAnsatt: NavAnsatt, foedselsnummer: Foedselsnummer): Boolean {
        logger.info("NAV-ansatt forsøker å hente informasjon om bruker: $foedselsnummer")

        val harNavAnsattTilgang = poaoTilgangHttpClient.evaluatePolicy(
            NavAnsattTilgangTilEksternBrukerPolicyInput(
                navAnsatt.azureId,
                TilgangType.LESE,
                foedselsnummer.foedselsnummer
            )
        ).getOrThrow()
            .isPermit

        if (!harNavAnsattTilgang) {
            logger.warn("NAV-ansatt har ikke tilgang til bruker: $foedselsnummer (v/poao-tilgang)")
            autitLogger.info(
                auditLogMelding(
                    foedselsnummer,
                    navAnsatt,
                    "NAV-ansatt har ikke tilgang til bruker (v/poao-tilgang)"
                )
            )
        } else {
            logger.info("NAV-ansatt har hentet informasjon om bruker: $foedselsnummer")
            autitLogger.info(
                auditLogMelding(
                    foedselsnummer,
                    navAnsatt,
                    "NAV-ansatt har hentet informasjon om bruker"
                )
            )
        }

        return harNavAnsattTilgang
    }
}
