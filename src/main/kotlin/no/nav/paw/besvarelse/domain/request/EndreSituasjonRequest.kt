package no.nav.paw.besvarelse.domain.request

import no.nav.paw.besvarelse.domain.besvarelse.DinSituasjonSvar
import no.nav.paw.besvarelse.domain.besvarelse.DinSituasjonTilleggsData
import no.nav.paw.besvarelse.domain.besvarelse.GjelderFra
import no.nav.paw.besvarelse.domain.besvarelse.GjelderTil

data class EndreSituasjonRequest(
    val dinSituasjon: DinSituasjon
)
data class DinSituasjon(
    val verdi: DinSituasjonSvar,
    val tilleggsData: DinSituasjonTilleggsData,
    val gjelderFra: GjelderFra,
    val gjelderTil: GjelderTil
)
