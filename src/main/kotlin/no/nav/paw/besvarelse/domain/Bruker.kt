package no.nav.paw.besvarelse.domain

import no.nav.paw.pdl.graphql.generated.enums.IdentGruppe
import no.nav.paw.pdl.graphql.generated.hentidenter.IdentInformasjon

data class Bruker(
    val foedselsnummer: Foedselsnummer,
    val identInformasjon: List<IdentInformasjon>?,
    val aktorId: AktorId = AktorId("IKKE_SATT")
) {
    private val historiskeFoedselsnummer: List<String>
        get() = identInformasjon
            ?.filter { it.gruppe == IdentGruppe.FOLKEREGISTERIDENT }
            ?.map { it.ident }
            .orEmpty()

    val alleFoedselsnummer = (historiskeFoedselsnummer + listOf(foedselsnummer.foedselsnummer)).distinct()
}
