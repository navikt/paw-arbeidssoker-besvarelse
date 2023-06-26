package no.nav.paw.besvarelse.services

import kotlinx.coroutines.runBlocking
import no.nav.paw.besvarelse.domain.ArbeidssokerRegistrert
import no.nav.paw.besvarelse.domain.Bruker
import no.nav.paw.besvarelse.domain.Foedselsnummer
import no.nav.paw.besvarelse.domain.besvarelse.EndretAv
import no.nav.paw.besvarelse.domain.request.EndreSituasjonRequest
import no.nav.paw.besvarelse.domain.response.ArbeidssokerRegistrertResponse
import no.nav.paw.besvarelse.kafka.producer.ArbeidssokerBesvarelseProducer
import no.nav.paw.besvarelse.repository.ArbeidssokerRegistrertRepository
import no.nav.paw.pdl.PdlClient
import no.nav.paw.pdl.hentIdenter

class ArbeidssokerRegistrertService(
    private val arbeidssokerRegistrertRepository: ArbeidssokerRegistrertRepository,
    private val arbeidssokerBesvarelseProducer: ArbeidssokerBesvarelseProducer,
    private val pdlClient: PdlClient
) {
    fun hentSiste(foedselsnummer: Foedselsnummer): ArbeidssokerRegistrertResponse {
        val identitetsListe = runBlocking { pdlClient.hentIdenter(foedselsnummer.foedselsnummer) }
        val bruker = Bruker(foedselsnummer, identitetsListe)

        return arbeidssokerRegistrertRepository.hentSiste(bruker).tilArbeidssokerRegistrertResponse()
    }

    fun opprettArbeidssokerRegistrert(arbeidssokerRegistrert: ArbeidssokerRegistrert) {
        val identitetsListe = runBlocking { pdlClient.hentIdenter(arbeidssokerRegistrert.bruker.foedselsnummer.foedselsnummer) }
        val bruker = Bruker(arbeidssokerRegistrert.bruker.foedselsnummer, identitetsListe, arbeidssokerRegistrert.bruker.aktorId)

        arbeidssokerRegistrertRepository.opprett(bruker, arbeidssokerRegistrert.tilArbeidssokerRegistrertEntity()).also {
            arbeidssokerBesvarelseProducer.publish(it.tilArbeidssokerBesvarelseEvent())
        }
    }

    fun endreSituasjon(
        foedselsnummer: Foedselsnummer,
        endreSituasjonRequest: EndreSituasjonRequest,
        endretAv: EndretAv
    ): ArbeidssokerRegistrertResponse {
        val identitetsListe = runBlocking { pdlClient.hentIdenter(foedselsnummer.foedselsnummer) }
        val bruker = Bruker(foedselsnummer, identitetsListe)

        return arbeidssokerRegistrertRepository.endreSituasjon(bruker, endreSituasjonRequest, endretAv).also {
            arbeidssokerBesvarelseProducer.publish(it.tilArbeidssokerBesvarelseEvent())
        }.tilArbeidssokerRegistrertResponse()
    }
}
