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
import no.nav.paw.besvarelse.domain.request.EndreBesvarelseRequest
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
                        "SELECT * FROM $ARBEIDSSOKER_REGISTRERT_TABELL WHERE foedselsnummer = ? ORDER BY opprettet DESC LIMIT 1",
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
                        """INSERT INTO $ARBEIDSSOKER_REGISTRERT_TABELL(foedselsnummer, aktor_id, registrerings_id, besvarelse, registrering_opprettet, opprettet_av, endret_av)
                            |VALUES (?, ?, ?::int, ?::jsonb, ?, ?, ?)
                        """.trimMargin(),
                        arbeidssokerRegistrertEntity.foedselsnummer.foedselsnummer,
                        arbeidssokerRegistrertEntity.aktorId.aktorId,
                        arbeidssokerRegistrertEntity.registreringsId,
                        objectMapper.writeValueAsString(arbeidssokerRegistrertEntity.besvarelse),
                        arbeidssokerRegistrertEntity.registreringOpprettet,
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

    fun endreSituasjon(foedselsnummer: Foedselsnummer, endreBesvarelseRequest: EndreBesvarelseRequest, endretAv: EndretAv): ArbeidssokerRegistrertEntity {
        logger.info("Endrer situasjon i besvarelsen i databasen")

        val arbeidssokerRegistrert = hentSiste(foedselsnummer)
            .copy(endretAv = endretAv)

        if (arbeidssokerRegistrert.besvarelse.dinSituasjon?.verdi == endreBesvarelseRequest.besvarelse.dinSituasjon.verdi) {
            logger.info("Forsøker å oppdatere 'dinSituasjon', men din situasjon er allerede endret")
            throw StatusException(HttpStatusCode.Conflict, "Situasjonen du forsøkte å endre er allerede satt")
        }

        val endretBesvarelse = arbeidssokerRegistrert.besvarelse
            .copy(
                dinSituasjon = DinSituasjon(
                    verdi = endreBesvarelseRequest.besvarelse.dinSituasjon.verdi,
                    gjelderFra = endreBesvarelseRequest.besvarelse.dinSituasjon.gjelderFra,
                    gjelderTil = endreBesvarelseRequest.besvarelse.dinSituasjon.gjelderTil,
                    endretAv = endretAv,
                    endret = LocalDateTime.now()
                )
            )

        val arbeidssokerRegistrertEndret = arbeidssokerRegistrert.copy(besvarelse = endretBesvarelse)
        opprett(arbeidssokerRegistrertEndret)

        return arbeidssokerRegistrertEndret
    }

    private fun Row.tilBesvarelseEntity() = ArbeidssokerRegistrertEntity(
        int("id"),
        Foedselsnummer(string("foedselsnummer")),
        AktorId(string("aktor_id")),
        int("registrerings_id"),
        objectMapper.readValue(string("besvarelse")),
        localDateTime("opprettet"),
        localDateTime("registrering_opprettet"),
        EndretAv.valueOf(string("opprettet_av")),
        EndretAv.valueOf(string("endret_av"))
    )

    companion object {
        const val ARBEIDSSOKER_REGISTRERT_TABELL = "arbeidssoker_registrert"
    }
}
