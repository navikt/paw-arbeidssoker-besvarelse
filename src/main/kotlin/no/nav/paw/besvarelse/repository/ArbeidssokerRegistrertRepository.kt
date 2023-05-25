package no.nav.paw.besvarelse.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.HttpStatusCode
import kotliquery.Row
import kotliquery.queryOf
import kotliquery.sessionOf
import no.nav.paw.besvarelse.domain.AktorId
import no.nav.paw.besvarelse.domain.ArbeidssokerRegistrertEntity
import no.nav.paw.besvarelse.domain.Foedselsnummer
import no.nav.paw.besvarelse.domain.besvarelse.DinSituasjon
import no.nav.paw.besvarelse.domain.besvarelse.EndretAv
import no.nav.paw.besvarelse.domain.request.EndreSituasjonRequest
import no.nav.paw.besvarelse.plugins.StatusException
import no.nav.paw.besvarelse.utils.logger
import org.postgresql.util.PSQLException
import java.time.LocalDateTime
import javax.sql.DataSource

class ArbeidssokerRegistrertRepository(
    private val dataSource: DataSource,
    private val objectMapper: ObjectMapper
) {
    fun hentSiste(foedselsnummer: Foedselsnummer): ArbeidssokerRegistrertEntity {
        logger.info("Henter sist oppdaterte besvarelse fra database")

        try {
            sessionOf(dataSource).use { session ->
                val query =
                    queryOf(
                        "SELECT * FROM $ARBEIDSSOKER_REGISTRERT_TABELL WHERE foedselsnummer = ? ORDER BY endret_dato DESC LIMIT 1",
                        foedselsnummer.foedselsnummer
                    ).map { it.tilBesvarelseEntity() }.asSingle
                return session.run(query)
                    ?: throw StatusException(HttpStatusCode.NoContent)
            }
        } catch (error: PSQLException) {
            logger.error("Feil i databaseoperasjon ved henting av siste besvarelse ${error.message}", error)
            throw StatusException(HttpStatusCode.InternalServerError, error.message)
        }
    }

    fun opprett(arbeidssokerRegistrertEntity: ArbeidssokerRegistrertEntity): Long {
        logger.info("Oppretter ny besvarelse i database")

        try {
            sessionOf(dataSource, returnGeneratedKey = true).use { session ->
                val query =
                    queryOf(
                        """INSERT INTO $ARBEIDSSOKER_REGISTRERT_TABELL(foedselsnummer, aktor_id, registrerings_id, besvarelse, registrerings_dato, opprettet_av, endret_av)
                            |VALUES (?, ?, ?::int, ?::jsonb, ?, ?, ?)
                        """.trimMargin(),
                        arbeidssokerRegistrertEntity.foedselsnummer.foedselsnummer,
                        arbeidssokerRegistrertEntity.aktorId.aktorId,
                        arbeidssokerRegistrertEntity.registreringsId,
                        objectMapper.writeValueAsString(arbeidssokerRegistrertEntity.besvarelse),
                        arbeidssokerRegistrertEntity.registreringsDato,
                        arbeidssokerRegistrertEntity.opprettetAv.toString(),
                        arbeidssokerRegistrertEntity.endretAv.toString()
                    ).asUpdateAndReturnGeneratedKey
                return session.run(query)
                    ?: throw StatusException(HttpStatusCode.InternalServerError, "Ukjent feil ved oppretting i databasen")
            }
        } catch (error: PSQLException) {
            logger.error("Feil i databaseoperasjon ved oppretting av besvarelse ${error.message}", error)
            throw StatusException(HttpStatusCode.InternalServerError, error.message)
        }
    }

    fun endreSituasjon(foedselsnummer: Foedselsnummer, endreSituasjonRequest: EndreSituasjonRequest, endretAv: EndretAv): ArbeidssokerRegistrertEntity {
        logger.info("Endrer situasjon i besvarelsen i databasen")

        val arbeidssokerRegistrert = hentSiste(foedselsnummer)
            .copy(endretAv = endretAv)

        val endretBesvarelse = arbeidssokerRegistrert.besvarelse
            .copy(
                dinSituasjon = DinSituasjon(
                    verdi = endreSituasjonRequest.dinSituasjon.verdi,
                    tilleggsData = endreSituasjonRequest.dinSituasjon.tilleggsData,
                    gjelderFraDato = endreSituasjonRequest.dinSituasjon.gjelderFraDato,
                    gjelderTilDato = endreSituasjonRequest.dinSituasjon.gjelderTilDato,
                    endretAv = endretAv,
                    endretDato = LocalDateTime.now()
                )
            )

        val arbeidssokerRegistrertEndret = arbeidssokerRegistrert.copy(besvarelse = endretBesvarelse)
        opprett(arbeidssokerRegistrertEndret)

        return hentSiste(foedselsnummer)
    }

    private fun Row.tilBesvarelseEntity() = ArbeidssokerRegistrertEntity(
        int("id"),
        Foedselsnummer(string("foedselsnummer")),
        AktorId(string("aktor_id")),
        int("registrerings_id"),
        objectMapper.readValue(string("besvarelse")),
        localDateTime("endret_dato"),
        localDateTime("registrerings_dato"),
        EndretAv.valueOf(string("opprettet_av")),
        EndretAv.valueOf(string("endret_av"))
    )

    companion object {
        const val ARBEIDSSOKER_REGISTRERT_TABELL = "arbeidssoker_registrert"
    }
}
