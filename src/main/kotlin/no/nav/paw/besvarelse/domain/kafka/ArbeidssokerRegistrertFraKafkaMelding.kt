package no.nav.paw.besvarelse.domain.kafka

import no.nav.paw.besvarelse.domain.AktorId
import no.nav.paw.besvarelse.domain.ArbeidssokerRegistrert
import no.nav.paw.besvarelse.domain.Foedselsnummer
import no.nav.paw.besvarelse.domain.besvarelse.AndreForhold
import no.nav.paw.besvarelse.domain.besvarelse.AndreForholdSvar
import no.nav.paw.besvarelse.domain.besvarelse.Besvarelse
import no.nav.paw.besvarelse.domain.besvarelse.DinSituasjon
import no.nav.paw.besvarelse.domain.besvarelse.DinSituasjonSvar
import no.nav.paw.besvarelse.domain.besvarelse.EndretAv
import no.nav.paw.besvarelse.domain.besvarelse.FremtidigSituasjon
import no.nav.paw.besvarelse.domain.besvarelse.FremtidigSituasjonSvar
import no.nav.paw.besvarelse.domain.besvarelse.HelseHinder
import no.nav.paw.besvarelse.domain.besvarelse.HelseHinderSvar
import no.nav.paw.besvarelse.domain.besvarelse.SisteStilling
import no.nav.paw.besvarelse.domain.besvarelse.SisteStillingSvar
import no.nav.paw.besvarelse.domain.besvarelse.TilbakeIArbeid
import no.nav.paw.besvarelse.domain.besvarelse.TilbakeIArbeidSvar
import no.nav.paw.besvarelse.domain.besvarelse.Utdanning
import no.nav.paw.besvarelse.domain.besvarelse.UtdanningBestatt
import no.nav.paw.besvarelse.domain.besvarelse.UtdanningBestattSvar
import no.nav.paw.besvarelse.domain.besvarelse.UtdanningGodkjent
import no.nav.paw.besvarelse.domain.besvarelse.UtdanningGodkjentSvar
import no.nav.paw.besvarelse.domain.besvarelse.UtdanningSvar
import java.time.ZonedDateTime

data class ArbeidssokerRegistrertFraKafkaMelding(
    val foedselsnummer: Foedselsnummer,
    val aktorId: AktorId,
    val registreringsId: Int,
    val besvarelse: BesvarelseFraKafkaMelding,
    val opprettetDato: ZonedDateTime,
    val opprettetAv: EndretAv = EndretAv.SYSTEM
) {
    fun tilArbeidssokerRegistrert() = ArbeidssokerRegistrert(
        foedselsnummer,
        aktorId,
        registreringsId,
        besvarelse.tilBesvarelse(),
        opprettetDato,
        opprettetAv
    )
}

data class BesvarelseFraKafkaMelding(
    val utdanning: UtdanningSvar? = null,
    val utdanningBestatt: UtdanningBestattSvar? = null,
    val utdanningGodkjent: UtdanningGodkjentSvar? = null,
    val helseHinder: HelseHinderSvar? = null,
    val andreForhold: AndreForholdSvar? = null,
    val sisteStilling: SisteStillingSvar? = null,
    val dinSituasjon: DinSituasjonSvar? = null,
    val fremtidigSituasjon: FremtidigSituasjonSvar? = null,
    val tilbakeIArbeid: TilbakeIArbeidSvar? = null
) {
    fun tilBesvarelse() = Besvarelse(
        Utdanning(utdanning),
        UtdanningBestatt(utdanningBestatt),
        UtdanningGodkjent(utdanningGodkjent),
        HelseHinder(helseHinder),
        AndreForhold(andreForhold),
        SisteStilling(sisteStilling),
        DinSituasjon(dinSituasjon),
        FremtidigSituasjon(fremtidigSituasjon),
        TilbakeIArbeid(tilbakeIArbeid)
    )
}
