package no.nav.paw.besvarelse.domain.request

import no.nav.paw.besvarelse.domain.besvarelse.DinSituasjonSvar
import no.nav.paw.besvarelse.domain.besvarelse.DinSituasjonTilleggsData
import no.nav.paw.besvarelse.domain.besvarelse.GjelderFraDato
import no.nav.paw.besvarelse.domain.besvarelse.GjelderTilDato

data class EndreSituasjonRequest(
    val dinSituasjon: DinSituasjon
)
data class DinSituasjon(
    val verdi: DinSituasjonSvar,
    val tilleggsData: DinSituasjonTilleggsData? = null,
    val gjelderFraDato: GjelderFraDato = null,
    val gjelderTilDato: GjelderTilDato = null
)
