package no.nav.paw.besvarelse.domain

import no.nav.paw.besvarelse.domain.besvarelse.Besvarelse
import no.nav.paw.besvarelse.domain.besvarelse.EndretAv
import java.time.LocalDateTime

data class ArbeidssokerRegistrert(
    val bruker: Bruker,
    val registreringsId: Int,
    val besvarelse: Besvarelse,
    val opprettetDato: LocalDateTime,
    val opprettetAv: EndretAv
) {
    fun tilArbeidssokerRegistrertEntity() = ArbeidssokerRegistrertEntity(
        null,
        bruker.foedselsnummer,
        bruker.aktorId,
        registreringsId,
        besvarelse,
        null,
        opprettetDato,
        opprettetAv,
        EndretAv.SYSTEM
    )
}
