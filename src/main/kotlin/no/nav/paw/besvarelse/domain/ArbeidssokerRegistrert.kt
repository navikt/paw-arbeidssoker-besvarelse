package no.nav.paw.besvarelse.domain

import no.nav.paw.besvarelse.domain.besvarelse.Besvarelse
import no.nav.paw.besvarelse.domain.besvarelse.EndretAv
import java.time.ZonedDateTime

data class ArbeidssokerRegistrert(
    val foedselsnummer: Foedselsnummer,
    val aktorId: AktorId,
    val registreringsId: Int,
    val besvarelse: Besvarelse,
    val opprettetDato: ZonedDateTime,
    val opprettetAv: EndretAv
) {
    fun tilArbeidssokerRegistrertEntity() = ArbeidssokerRegistrertEntity(
        null,
        foedselsnummer,
        aktorId,
        registreringsId,
        besvarelse,
        null,
        opprettetDato.toLocalDateTime(),
        opprettetAv,
        EndretAv.SYSTEM
    )
}
