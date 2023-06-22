package no.nav.paw.besvarelse.domain

import no.nav.paw.besvarelse.AndreForhold
import no.nav.paw.besvarelse.AndreForholdSvar
import no.nav.paw.besvarelse.ArbeidssokerBesvarelseEvent
import no.nav.paw.besvarelse.DinSituasjon
import no.nav.paw.besvarelse.DinSituasjonSvar
import no.nav.paw.besvarelse.DinSituasjonTilleggsData
import no.nav.paw.besvarelse.HelseHinder
import no.nav.paw.besvarelse.HelseHinderSvar
import no.nav.paw.besvarelse.OpprettetAv
import no.nav.paw.besvarelse.SisteStilling
import no.nav.paw.besvarelse.SisteStillingSvar
import no.nav.paw.besvarelse.Utdanning
import no.nav.paw.besvarelse.UtdanningBestatt
import no.nav.paw.besvarelse.UtdanningBestattSvar
import no.nav.paw.besvarelse.UtdanningGodkjent
import no.nav.paw.besvarelse.UtdanningGodkjentSvar
import no.nav.paw.besvarelse.UtdanningSvar
import no.nav.paw.besvarelse.domain.besvarelse.Besvarelse
import no.nav.paw.besvarelse.domain.besvarelse.EndretAv
import no.nav.paw.besvarelse.domain.response.ArbeidssokerRegistrertResponse
import java.time.LocalDateTime
import java.time.ZoneOffset

data class ArbeidssokerRegistrertEntity(
    val id: Int? = null,
    val foedselsnummer: Foedselsnummer,
    val aktorId: AktorId,
    val registreringsId: Int,
    val besvarelse: Besvarelse,
    val endretTidspunkt: LocalDateTime? = null,
    val registreringsTidspunkt: LocalDateTime? = null,
    val opprettetAv: EndretAv,
    val endretAv: EndretAv,
    val erBesvarelsenEndret: Boolean = false
) {
    fun tilArbeidssokerRegistrertResponse() = ArbeidssokerRegistrertResponse(
        registreringsId,
        besvarelse,
        endretAv,
        endretTidspunkt,
        registreringsTidspunkt,
        opprettetAv,
        erBesvarelsenEndret
    )

    fun tilArbeidssokerBesvarelseEvent() = ArbeidssokerBesvarelseEvent(
        id,
        registreringsId,
        foedselsnummer.foedselsnummer,
        aktorId.aktorId,
        endretTidspunkt?.toInstant(ZoneOffset.ofHours(1)),
        registreringsTidspunkt?.toInstant(ZoneOffset.ofHours(1)),
        OpprettetAv.valueOf(opprettetAv.toString()),
        no.nav.paw.besvarelse.EndretAv.valueOf(endretAv.toString()),
        erBesvarelsenEndret,
        no.nav.paw.besvarelse.Besvarelse(
            Utdanning(
                besvarelse.utdanning?.endretTidspunkt?.toInstant(ZoneOffset.ofHours(1)),
                besvarelse.utdanning?.endretAv?.toString(),
                besvarelse.utdanning?.gjelderFraDato,
                besvarelse.utdanning?.gjelderTilDato,
                UtdanningSvar.valueOf(besvarelse.utdanning?.verdi.toString())
            ),
            UtdanningBestatt(
                besvarelse.utdanningBestatt?.endretTidspunkt?.toInstant(ZoneOffset.ofHours(1)),
                besvarelse.utdanningBestatt?.endretAv?.toString(),
                besvarelse.utdanningBestatt?.gjelderFraDato,
                besvarelse.utdanningBestatt?.gjelderTilDato,
                UtdanningBestattSvar.valueOf(besvarelse.utdanningBestatt?.verdi.toString())
            ),
            UtdanningGodkjent(
                besvarelse.utdanningGodkjent?.endretTidspunkt?.toInstant(ZoneOffset.ofHours(1)),
                besvarelse.utdanningGodkjent?.endretAv?.toString(),
                besvarelse.utdanningGodkjent?.gjelderFraDato,
                besvarelse.utdanningGodkjent?.gjelderTilDato,
                UtdanningGodkjentSvar.valueOf(besvarelse.utdanningGodkjent?.verdi.toString())
            ),
            HelseHinder(
                besvarelse.helseHinder?.endretTidspunkt?.toInstant(ZoneOffset.ofHours(1)),
                besvarelse.helseHinder?.endretAv?.toString(),
                besvarelse.helseHinder?.gjelderFraDato,
                besvarelse.helseHinder?.gjelderTilDato,
                HelseHinderSvar.valueOf(besvarelse.helseHinder?.verdi.toString())
            ),
            AndreForhold(
                besvarelse.andreForhold?.endretTidspunkt?.toInstant(ZoneOffset.ofHours(1)),
                besvarelse.andreForhold?.endretAv?.toString(),
                besvarelse.andreForhold?.gjelderFraDato,
                besvarelse.andreForhold?.gjelderTilDato,
                AndreForholdSvar.valueOf(besvarelse.andreForhold?.verdi.toString())
            ),
            SisteStilling(
                besvarelse.sisteStilling?.endretTidspunkt?.toInstant(ZoneOffset.ofHours(1)),
                besvarelse.sisteStilling?.endretAv?.toString(),
                besvarelse.sisteStilling?.gjelderFraDato,
                besvarelse.sisteStilling?.gjelderTilDato,
                SisteStillingSvar.valueOf(besvarelse.sisteStilling?.verdi.toString())
            ),
            DinSituasjon(
                besvarelse.dinSituasjon?.endretTidspunkt?.toInstant(ZoneOffset.ofHours(1)),
                besvarelse.dinSituasjon?.endretAv?.toString(),
                besvarelse.dinSituasjon?.gjelderFraDato,
                besvarelse.dinSituasjon?.gjelderTilDato,
                DinSituasjonSvar.valueOf(besvarelse.dinSituasjon?.verdi.toString()),
                DinSituasjonTilleggsData(
                    besvarelse.dinSituasjon?.tilleggsData?.forsteArbeidsdagDato,
                    besvarelse.dinSituasjon?.tilleggsData?.sisteArbeidsdagDato,
                    besvarelse.dinSituasjon?.tilleggsData?.oppsigelseDato,
                    besvarelse.dinSituasjon?.tilleggsData?.gjelderFraDato,
                    besvarelse.dinSituasjon?.tilleggsData?.permitteringsProsent,
                    besvarelse.dinSituasjon?.tilleggsData?.stillingsProsent

                )
            )
        )
    )
}
