package no.nav.paw.besvarelse.domain

data class Foedselsnummer(val foedselsnummer: String) {
    override fun toString(): String = "*".repeat(11)
}
