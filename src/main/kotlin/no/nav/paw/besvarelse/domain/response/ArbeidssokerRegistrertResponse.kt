package no.nav.paw.besvarelse.domain.response

import no.nav.paw.besvarelse.domain.besvarelse.Besvarelse
import no.nav.paw.besvarelse.domain.besvarelse.EndretAv
import java.time.LocalDateTime

data class ArbeidssokerRegistrertResponse(
    val registreringsId: Int,
    val besvarelse: Besvarelse,
    val endretAv: EndretAv,
    val endret: LocalDateTime?,
    val registreringOpprettet: LocalDateTime?,
    val opprettetAv: EndretAv
)
