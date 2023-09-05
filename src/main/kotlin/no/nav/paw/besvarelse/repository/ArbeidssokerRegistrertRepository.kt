package no.nav.paw.besvarelse.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.HttpStatusCode
import kotliquery.Row
import kotliquery.queryOf
import kotliquery.sessionOf
import no.nav.paw.besvarelse.domain.AktorId
import no.nav.paw.besvarelse.domain.ArbeidssokerRegistrertEntity
import no.nav.paw.besvarelse.domain.Bruker
import no.nav.paw.besvarelse.domain.Foedselsnummer
import no.nav.paw.besvarelse.domain.besvarelse.DinSituasjon
import no.nav.paw.besvarelse.domain.besvarelse.EndretAv
import no.nav.paw.besvarelse.domain.request.EndreSituasjonRequest
import no.nav.paw.besvarelse.plugins.BesvarelseNotFound
import no.nav.paw.besvarelse.plugins.StatusException
import no.nav.paw.besvarelse.utils.logger
import org.postgresql.util.PSQLException
import java.time.LocalDateTime
import javax.sql.DataSource

class ArbeidssokerRegistrertRepository(
    private val dataSource: DataSource,
    private val objectMapper: ObjectMapper
) {
    fun hentSiste(bruker: Bruker): ArbeidssokerRegistrertEntity {
        logger.info("Henter sist oppdaterte besvarelse fra database")

        try {
            sessionOf(dataSource).use { session ->
                val query =
                    queryOf(
                        "SELECT * FROM $ARBEIDSSOKER_REGISTRERT_TABELL WHERE foedselsnummer IN (${
                            bruker.alleFoedselsnummer.joinToString(
                                separator = ","
                            ) { s -> "\'$s\'" }
                        }) ORDER BY endret_tidspunkt DESC LIMIT 1"
                    ).map { it.tilBesvarelseEntity() }.asSingle

                return session.run(query)
                    ?: throw BesvarelseNotFound()
            }
        } catch (error: PSQLException) {
            logger.error("Feil i databaseoperasjon ved henting av siste besvarelse ${error.message}", error)
            throw StatusException(HttpStatusCode.InternalServerError, error.message)
        }
    }

    fun opprett(
        bruker: Bruker,
        arbeidssokerRegistrertEntity: ArbeidssokerRegistrertEntity,
        endret: Boolean = false
    ): ArbeidssokerRegistrertEntity {
        logger.info("Oppretter ny besvarelse i database")

        try {
            sessionOf(dataSource, returnGeneratedKey = true).use { session ->
                val query =
                    queryOf(
                        """INSERT INTO $ARBEIDSSOKER_REGISTRERT_TABELL(foedselsnummer, aktor_id, registrerings_id, besvarelse, registrerings_tidspunkt, opprettet_av, endret_av, er_besvarelsen_endret)
                            |VALUES (?, ?, ?::int, ?::jsonb, ?, ?, ?, ?)
                        """.trimMargin(),
                        arbeidssokerRegistrertEntity.foedselsnummer.foedselsnummer,
                        arbeidssokerRegistrertEntity.aktorId.aktorId,
                        arbeidssokerRegistrertEntity.registreringsId,
                        objectMapper.writeValueAsString(arbeidssokerRegistrertEntity.besvarelse),
                        arbeidssokerRegistrertEntity.registreringsTidspunkt,
                        arbeidssokerRegistrertEntity.opprettetAv.toString(),
                        arbeidssokerRegistrertEntity.endretAv.toString(),
                        endret
                    ).asUpdateAndReturnGeneratedKey
                session.run(query)
                    ?: throw StatusException(
                        HttpStatusCode.InternalServerError,
                        "Ukjent feil ved oppretting i databasen"
                    )
                return hentSiste(bruker)
            }
        } catch (error: PSQLException) {
            logger.error("Feil i databaseoperasjon ved oppretting av besvarelse ${error.message}", error)
            throw StatusException(HttpStatusCode.InternalServerError, error.message)
        }
    }

    fun endreSituasjon(
        bruker: Bruker,
        endreSituasjonRequest: EndreSituasjonRequest,
        endretAv: EndretAv
    ): ArbeidssokerRegistrertEntity {
        logger.info("Endrer situasjon i besvarelsen i databasen")

        val arbeidssokerRegistrert = hentSiste(bruker)
            .copy(endretAv = endretAv)

        val endretBesvarelse = arbeidssokerRegistrert.besvarelse
            .copy(
                dinSituasjon = DinSituasjon(
                    verdi = endreSituasjonRequest.dinSituasjon.verdi,
                    tilleggsData = endreSituasjonRequest.dinSituasjon.tilleggsData,
                    gjelderFraDato = endreSituasjonRequest.dinSituasjon.gjelderFraDato,
                    gjelderTilDato = endreSituasjonRequest.dinSituasjon.gjelderTilDato,
                    endretAv = endretAv,
                    endretTidspunkt = LocalDateTime.now()
                )
            )

        val arbeidssokerRegistrertEndret = arbeidssokerRegistrert.copy(besvarelse = endretBesvarelse)
        return opprett(bruker, arbeidssokerRegistrertEndret, true)
    }

    private fun Row.tilBesvarelseEntity() = ArbeidssokerRegistrertEntity(
        int("id"),
        Foedselsnummer(string("foedselsnummer")),
        AktorId(string("aktor_id")),
        int("registrerings_id"),
        objectMapper.readValue(string("besvarelse")),
        localDateTime("endret_tidspunkt"),
        localDateTime("registrerings_tidspunkt"),
        EndretAv.valueOf(string("opprettet_av")),
        EndretAv.valueOf(string("endret_av")),
        boolean("er_besvarelsen_endret")
    )

    companion object {
        const val ARBEIDSSOKER_REGISTRERT_TABELL = "arbeidssoker_registrert"
    }
}
