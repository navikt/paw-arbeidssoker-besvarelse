package no.nav.paw.besvarelse.services

import kotlinx.coroutines.runBlocking
import no.nav.paw.besvarelse.utils.CallId
import no.nav.paw.pdl.PdlClient
import no.nav.paw.pdl.graphql.generated.hentidenter.IdentInformasjon
import no.nav.paw.pdl.hentIdenter

private const val CONSUMER_ID = "paw-arbeidssoker-besvarelse"
private const val BEHANDLINGSNUMMER_ARBEIDSSOEKERREGISTRERING = "B452" // Fra https://behandlingskatalog.intern.nav.no/process/purpose/ARBEIDSMARKEDSTILTAK

class PdlService(private val pdlClient: PdlClient) {

    fun hentIdenter(ident: String): List<IdentInformasjon>? {
        return runBlocking { pdlClient.hentIdenter(ident, CallId.callId, CONSUMER_ID, BEHANDLINGSNUMMER_ARBEIDSSOEKERREGISTRERING) }
    }
}
