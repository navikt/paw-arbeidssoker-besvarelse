package no.nav.paw.besvarelse.domain

import no.nav.paw.besvarelse.ArbeidssokerBesvarelseEndretEvent
import no.nav.paw.besvarelse.domain.besvarelse.Besvarelse
import no.nav.paw.besvarelse.domain.besvarelse.EndretAv
import no.nav.paw.besvarelse.domain.response.ArbeidssokerRegistrertResponse
import java.time.LocalDateTime

data class ArbeidssokerRegistrertEntity(
    val id: Int? = null,
    val foedselsnummer: Foedselsnummer,
    val aktorId: AktorId,
    val registreringsId: Int,
    val besvarelse: Besvarelse,
    val opprettet: LocalDateTime? = null,
    val registreringOpprettet: LocalDateTime? = null,
    val opprettetAv: EndretAv,
    val endretAv: EndretAv
) {
    fun tilArbeidssokerRegistrertResponse() = ArbeidssokerRegistrertResponse(
        registreringsId,
        besvarelse,
        endretAv,
        opprettet,
        registreringOpprettet,
        opprettetAv
    )

    fun tilArbeidssokerBesvarelseEndretEvent() = ArbeidssokerBesvarelseEndretEvent(
        id,
        registreringsId,
        foedselsnummer.foedselsnummer,
        aktorId.aktorId,
        null
    )
}
