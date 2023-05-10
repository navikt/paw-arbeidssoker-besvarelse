package no.nav.paw.besvarelse.utils

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.MappingIterator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import no.nav.paw.besvarelse.domain.AktorId
import no.nav.paw.besvarelse.domain.ArbeidssokerRegistrert
import no.nav.paw.besvarelse.domain.Foedselsnummer
import no.nav.paw.besvarelse.domain.besvarelse.AndreForhold
import no.nav.paw.besvarelse.domain.besvarelse.AndreForholdSvar
import no.nav.paw.besvarelse.domain.besvarelse.Besvarelse
import no.nav.paw.besvarelse.domain.besvarelse.DinSituasjon
import no.nav.paw.besvarelse.domain.besvarelse.DinSituasjonSvar
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
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId

data class TekstForSporsmal(
    val sporsmalId: String,
    val sporsmal: String,
    val svar: String
)

val nuskodeMap: Map<Int, UtdanningSvar> = mapOf(
    0 to UtdanningSvar.INGEN_UTDANNING,
    2 to UtdanningSvar.GRUNNSKOLE,
    3 to UtdanningSvar.VIDEREGAENDE_GRUNNUTDANNING,
    4 to UtdanningSvar.VIDEREGAENDE_FAGBREV_SVENNEBREV,
    6 to UtdanningSvar.HOYERE_UTDANNING_1_TIL_4,
    7 to UtdanningSvar.HOYERE_UTDANNING_5_ELLER_MER,
    9 to UtdanningSvar.INGEN_SVAR
)

data class UserData(
    @JsonProperty("bruker_registrering_id") val registreringsId: Int,
    @JsonProperty("aktor_id") val aktorId: String,
    @JsonProperty("opprettet_dato")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    val opprettetDato: LocalDateTime,
    @JsonProperty("nus_kode") val nusKode: Int,
    @JsonProperty("yrkespraksis") val yrkespraksis: String,
    @JsonProperty("yrkesbeskrivelse") val yrkesbeskrivelse: String,
    @JsonProperty("konsept_id") val konseptId: Int,
    @JsonProperty("andre_utfordringer") val andreForhold: AndreForholdSvar,
    @JsonProperty("begrunnelse_for_registrering") val dinSituasjon: DinSituasjonSvar,
    @JsonProperty("utdanning_bestatt") val utdanningBestatt: UtdanningBestattSvar,
    @JsonProperty("utdanning_godkjent_norge") val utdanningGodkjent: UtdanningGodkjentSvar,
    @JsonProperty("jobbhistorikk") val sisteStilling: SisteStillingSvar,
    @JsonProperty("har_helseutfordringer") val helseHinder: HelseHinderSvar,
    @JsonProperty("tekster_for_besvarelse") val teksterForBesvarelse: String,
    @JsonProperty("foedselsnummer") val foedselsnummer: String = "12345678910",
    @JsonProperty("opprettet_av") val opprettetAv: EndretAv
) {
    fun tilArbeidssokerRegistrert(): ArbeidssokerRegistrert {
        // val mapper = ObjectMapper().findAndRegisterModules()
        // val besvarelse: List<TekstForSporsmal> = mapper.readValue(teksterForBesvarelse)

        return ArbeidssokerRegistrert(
            Foedselsnummer(foedselsnummer),
            AktorId(aktorId),
            registreringsId,
            Besvarelse(
                Utdanning(nuskodeMap[nusKode]),
                UtdanningBestatt(utdanningBestatt),
                UtdanningGodkjent(utdanningGodkjent),
                HelseHinder(helseHinder),
                AndreForhold(andreForhold),
                SisteStilling(sisteStilling),
                DinSituasjon(dinSituasjon),
                null, // Gjelder kun sykemeldtregistrering?
                null // Gjelder kun sykemeldtregistrering?
            ),
            opprettetDato.atZone(ZoneId.of("Europe/Oslo")),
            opprettetAv
        )
    }
}

fun main() {
    val mapper = ObjectMapper().findAndRegisterModules()
    val file = File("src/main/resources/import_test.csv")
    val csvMapper = CsvMapper().findAndRegisterModules()

    val userDataList: List<ArbeidssokerRegistrert> = csvMapper
        .readerFor(UserData::class.java)
        .with(CsvSchema.emptySchema().withHeader().withColumnSeparator('\t'))
        .readValues<UserData>(file)
        .use { mappingIterator: MappingIterator<UserData> ->
            mappingIterator.readAll()
        }
        .map { it.tilArbeidssokerRegistrert() }

    // foedselsnummer, aktor_id, registrerings_id, besvarelse, endret_av
    userDataList.forEach {
        println("${it.foedselsnummer.foedselsnummer}\t${it.aktorId.aktorId}\t${it.registreringsId}\t${mapper.writeValueAsString(it.besvarelse)}\t${it.opprettetDato.toLocalDateTime()}\t${it.opprettetAv}")
    }
}
