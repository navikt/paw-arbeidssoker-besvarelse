package no.nav.paw.besvarelse.domain.besvarelse

import java.time.LocalDate
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
)
data class Utdanning(
    val verdi: UtdanningSvar? = null,
    val gjelderFraDato: GjelderFraDato = null,
    val gjelderTilDato: GjelderTilDato = null,
    val endretAv: EndretAv? = null,
    val endretDato: EndretDato? = null
)

data class UtdanningBestatt(
    val verdi: UtdanningBestattSvar? = null,
    val gjelderFraDato: GjelderFraDato = null,
    val gjelderTilDato: GjelderTilDato = null,
    val endretAv: EndretAv? = null,
    val endretDato: EndretDato? = null
)

data class UtdanningGodkjent(
    val verdi: UtdanningGodkjentSvar? = null,
    val gjelderFraDato: GjelderFraDato = null,
    val gjelderTilDato: GjelderTilDato = null,
    val endretAv: EndretAv? = null,
    val endretDato: EndretDato? = null
)

data class HelseHinder(
    val verdi: HelseHinderSvar? = null,
    val gjelderFraDato: GjelderFraDato = null,
    val gjelderTilDato: GjelderTilDato = null,
    val endretAv: EndretAv? = null,
    val endretDato: EndretDato? = null
)

data class AndreForhold(
    val verdi: AndreForholdSvar? = null,
    val gjelderFraDato: GjelderFraDato = null,
    val gjelderTilDato: GjelderTilDato = null,
    val endretAv: EndretAv? = null,
    val endretDato: EndretDato? = null
)

data class SisteStilling(
    val verdi: SisteStillingSvar? = null,
    val gjelderFraDato: GjelderFraDato = null,
    val gjelderTilDato: GjelderTilDato = null,
    val endretAv: EndretAv? = null,
    val endretDato: EndretDato? = null
)

data class DinSituasjon(
    val verdi: DinSituasjonSvar? = null,
    val tilleggsData: DinSituasjonTilleggsData? = null,
    val gjelderFraDato: GjelderFraDato = null,
    val gjelderTilDato: GjelderTilDato = null,
    val endretAv: EndretAv? = null,
    val endretDato: EndretDato? = null
)

data class DinSituasjonTilleggsData(
    val forsteArbeidsdagDato: LocalDate? = null,
    val sisteArbeidsdagDato: LocalDate? = null,
    val oppsigelseDato: LocalDate? = null,
    val gjelderFraDato: GjelderFraDato = null,
    val permitteringsProsent: String? = null
)

data class FremtidigSituasjon(
    val verdi: FremtidigSituasjonSvar? = null,
    val gjelderFraDato: GjelderFraDato = null,
    val gjelderTilDato: GjelderTilDato = null,
    val endretAv: EndretAv? = null,
    val endretDato: EndretDato? = null
)

data class TilbakeIArbeid(
    val verdi: TilbakeIArbeidSvar? = null,
    val gjelderFraDato: GjelderFraDato = null,
    val gjelderTilDato: GjelderTilDato = null,
    val endretAv: EndretAv? = null,
    val endretDato: EndretDato? = null
)

typealias GjelderFraDato = LocalDate?
typealias GjelderTilDato = LocalDate?
typealias EndretDato = LocalDateTime

enum class EndretAv { BRUKER, VEILEDER, SYSTEM }
