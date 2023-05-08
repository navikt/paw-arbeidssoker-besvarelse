package no.nav.paw.besvarelse.domain.besvarelse

import java.time.LocalDateTime
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

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
        val endretFields = this::class.memberProperties.mapNotNull { felt ->
            felt.returnType.classifier?.let { classifier ->
                (classifier as KClass<*>).memberProperties.filter { it.name == "endret" }
            }
        }.flatten()

        val alleEndretDatoer = endretFields.mapNotNull { it.getter.call(this) as? LocalDateTime }

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
