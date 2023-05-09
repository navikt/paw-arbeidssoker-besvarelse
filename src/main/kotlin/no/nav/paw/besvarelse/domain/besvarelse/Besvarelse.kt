package no.nav.paw.besvarelse.domain.besvarelse

import java.time.LocalDateTime

data class Besvarelse(
    val utdanning: Utdanning? = null,
    val utdanningBestatt: UtdanningBestatt? = null,
    val utdanningGodkjent: UtdanningGodkjent? = null,
    val helseHinder: HelseHinder? = null,
    val andreForhold: AndreForhold? = null,
    val sisteStilling: SisteStilling? = null,
    val dinSituasjon: DinSituasjon? = null,
    val fremtidigSituasjon: FremtidigSituasjon? = null,
    val tilbakeIArbeid: TilbakeIArbeid? = null
) {
    fun sisteEndret(): LocalDateTime? {
        val alleEndretDatoer = listOfNotNull(
            utdanning?.endret,
            utdanningBestatt?.endret,
            utdanningGodkjent?.endret,
            helseHinder?.endret,
            andreForhold?.endret,
            sisteStilling?.endret,
            dinSituasjon?.endret,
            fremtidigSituasjon?.endret,
            tilbakeIArbeid?.endret
        )

        return alleEndretDatoer.maxOrNull()
    }
}
data class Utdanning(
    val verdi: UtdanningSvar? = null,
    val gjelderFra: GjelderFra = null,
    val gjelderTil: GjelderTil = null,
    val endretAv: EndretAv? = null,
    val endret: Endret? = null
)

data class UtdanningBestatt(
    val verdi: UtdanningBestattSvar? = null,
    val gjelderFra: GjelderFra = null,
    val gjelderTil: GjelderTil = null,
    val endretAv: EndretAv? = null,
    val endret: Endret? = null
)

data class UtdanningGodkjent(
    val verdi: UtdanningGodkjentSvar? = null,
    val gjelderFra: GjelderFra = null,
    val gjelderTil: GjelderTil = null,
    val endretAv: EndretAv? = null,
    val endret: Endret? = null
)

data class HelseHinder(
    val verdi: HelseHinderSvar? = null,
    val gjelderFra: GjelderFra = null,
    val gjelderTil: GjelderTil = null,
    val endretAv: EndretAv? = null,
    val endret: Endret? = null
)

data class AndreForhold(
    val verdi: AndreForholdSvar? = null,
    val gjelderFra: GjelderFra = null,
    val gjelderTil: GjelderTil = null,
    val endretAv: EndretAv? = null,
    val endret: Endret? = null
)

data class SisteStilling(
    val verdi: SisteStillingSvar? = null,
    val gjelderFra: GjelderFra = null,
    val gjelderTil: GjelderTil = null,
    val endretAv: EndretAv? = null,
    val endret: Endret? = null
)

data class DinSituasjon(
    val verdi: DinSituasjonSvar? = null,
    val gjelderFra: GjelderFra = null,
    val gjelderTil: GjelderTil = null,
    val endretAv: EndretAv? = null,
    val endret: Endret? = null
)

data class FremtidigSituasjon(
    val verdi: FremtidigSituasjonSvar? = null,
    val gjelderFra: GjelderFra = null,
    val gjelderTil: GjelderTil = null,
    val endretAv: EndretAv? = null,
    val endret: Endret? = null
)

data class TilbakeIArbeid(
    val verdi: TilbakeIArbeidSvar? = null,
    val gjelderFra: GjelderFra = null,
    val gjelderTil: GjelderTil = null,
    val endretAv: EndretAv? = null,
    val endret: Endret? = null
)

typealias GjelderFra = LocalDateTime?
typealias GjelderTil = LocalDateTime?
typealias Endret = LocalDateTime

enum class EndretAv { BRUKER, VEILEDER, SYSTEM }
