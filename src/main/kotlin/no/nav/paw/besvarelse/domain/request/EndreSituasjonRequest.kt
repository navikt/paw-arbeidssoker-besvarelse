package no.nav.paw.besvarelse.domain.request

import no.nav.paw.besvarelse.domain.besvarelse.DinSituasjonSvar
import no.nav.paw.besvarelse.domain.besvarelse.GjelderFra
import no.nav.paw.besvarelse.domain.besvarelse.GjelderTil

data class EndreBesvarelseRequest(
    val besvarelse: Besvarelse
)

data class Besvarelse(
    val dinSituasjon: DinSituasjon
)
data class DinSituasjon(
    val dinSituasjon: DinSituasjonSvar,
    val gjelderFra: GjelderFra,
    val gjelderTil: GjelderTil
)
