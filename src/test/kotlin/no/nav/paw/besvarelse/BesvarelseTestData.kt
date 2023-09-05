package no.nav.paw.besvarelse

import no.nav.paw.besvarelse.domain.AktorId
import no.nav.paw.besvarelse.domain.ArbeidssokerRegistrert
import no.nav.paw.besvarelse.domain.ArbeidssokerRegistrertEntity
import no.nav.paw.besvarelse.domain.Bruker
import no.nav.paw.besvarelse.domain.Foedselsnummer
import no.nav.paw.besvarelse.domain.besvarelse.AndreForhold
import no.nav.paw.besvarelse.domain.besvarelse.AndreForholdSvar
import no.nav.paw.besvarelse.domain.besvarelse.Besvarelse
import no.nav.paw.besvarelse.domain.besvarelse.DinSituasjonSvar
import no.nav.paw.besvarelse.domain.besvarelse.DinSituasjonTilleggsData
import no.nav.paw.besvarelse.domain.besvarelse.EndretAv
import no.nav.paw.besvarelse.domain.besvarelse.HelseHinder
import no.nav.paw.besvarelse.domain.besvarelse.HelseHinderSvar
import no.nav.paw.besvarelse.domain.besvarelse.SisteStilling
import no.nav.paw.besvarelse.domain.besvarelse.SisteStillingSvar
import no.nav.paw.besvarelse.domain.besvarelse.Utdanning
import no.nav.paw.besvarelse.domain.besvarelse.UtdanningBestatt
import no.nav.paw.besvarelse.domain.besvarelse.UtdanningBestattSvar
import no.nav.paw.besvarelse.domain.besvarelse.UtdanningGodkjent
import no.nav.paw.besvarelse.domain.besvarelse.UtdanningGodkjentSvar
import no.nav.paw.besvarelse.domain.besvarelse.UtdanningSvar
import no.nav.paw.besvarelse.domain.request.DinSituasjon
import no.nav.paw.besvarelse.domain.request.EndreSituasjonRequest
import no.nav.paw.pdl.graphql.generated.enums.IdentGruppe
import no.nav.paw.pdl.graphql.generated.hentidenter.IdentInformasjon
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

object BesvarelseTestData {
    val foedselsnummer = Foedselsnummer("18908396568")
    val foedselsnummer2 = Foedselsnummer("18908396561")
    val aktorId = AktorId("2862185140226")
    val bruker = Bruker(foedselsnummer, emptyList(), aktorId)
    val brukerMedToFoedselsnummer = Bruker(foedselsnummer, listOf(IdentInformasjon(foedselsnummer2.foedselsnummer, IdentGruppe.FOLKEREGISTERIDENT)), aktorId)
    private val zonedDateTimeNow = ZonedDateTime.now(ZoneId.of("Europe/Oslo"))
    val besvarelse = Besvarelse(
        utdanning = Utdanning(UtdanningSvar.VIDEREGAENDE_FAGBREV_SVENNEBREV),
        utdanningBestatt = UtdanningBestatt(UtdanningBestattSvar.JA),
        utdanningGodkjent = UtdanningGodkjent(UtdanningGodkjentSvar.JA),
        helseHinder = HelseHinder(HelseHinderSvar.NEI),
        andreForhold = AndreForhold(AndreForholdSvar.NEI),
        sisteStilling = SisteStilling(SisteStillingSvar.HAR_HATT_JOBB),
        dinSituasjon = no.nav.paw.besvarelse.domain.besvarelse.DinSituasjon(DinSituasjonSvar.OPPSIGELSE)
    )
    val arbeidssokerRegistrert = ArbeidssokerRegistrert(bruker, 1, besvarelse, zonedDateTimeNow, EndretAv.BRUKER)
    val arbeidssokerRegistrertEntity = ArbeidssokerRegistrertEntity(
        foedselsnummer = foedselsnummer,
        aktorId = aktorId,
        registreringsId = 1,
        besvarelse = besvarelse,
        endretAv = EndretAv.BRUKER,
        opprettetAv = EndretAv.BRUKER,
        registreringsTidspunkt = LocalDateTime.now(),
        endretTidspunkt = LocalDateTime.now()
    )
    val endreSituasjonRequest = EndreSituasjonRequest(
        dinSituasjon = DinSituasjon(
            DinSituasjonSvar.ER_PERMITTERT,
            DinSituasjonTilleggsData(oppsigelseDato = LocalDate.now()),
            null,
            null
        )
    )
    val pdlClientHentIdenterResponse = """
        {
          "data": {
            "hentIdenter": {
              "identer": [
                {
                  "ident": "$foedselsnummer",
                  "gruppe": "FOLKEREGISTERIDENT"
                },
                {
                  "ident": "$foedselsnummer2",
                  "gruppe": "FOLKEREGISTERIDENT"
                },
                {
                  "ident": "$aktorId",
                  "gruppe": "AKTORID"
                }
              ]
            }
          }
        }
    """.trimIndent()
}
