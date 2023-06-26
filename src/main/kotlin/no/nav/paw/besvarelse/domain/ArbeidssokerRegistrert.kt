package no.nav.paw.besvarelse.domain

import no.nav.paw.besvarelse.domain.besvarelse.Besvarelse
import no.nav.paw.besvarelse.domain.besvarelse.EndretAv
import java.time.ZonedDateTime

data class ArbeidssokerRegistrert(
    val bruker: Bruker,
    val registreringsId: Int,
    val besvarelse: Besvarelse,
    val opprettetDato: ZonedDateTime,
    val opprettetAv: EndretAv
) {
    fun tilArbeidssokerRegistrertEntity() = ArbeidssokerRegistrertEntity(
        null,
        bruker.foedselsnummer,
        bruker.aktorId,
        registreringsId,
        besvarelse,
        null,
        opprettetDato.toLocalDateTime(),
        opprettetAv,
        EndretAv.SYSTEM
    )
}
