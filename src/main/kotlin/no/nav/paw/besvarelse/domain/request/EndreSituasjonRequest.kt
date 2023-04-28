package no.nav.paw.besvarelse.domain.request

import no.nav.paw.besvarelse.domain.besvarelse.DinSituasjonSvar

data class EndreSituasjonRequest(
    val dinSituasjon: DinSituasjonSvar
)
