package no.nav.paw.besvarelse.services

import no.nav.paw.besvarelse.domain.ArbeidssokerRegistrert
import no.nav.paw.besvarelse.domain.Bruker
import no.nav.paw.besvarelse.domain.Foedselsnummer
import no.nav.paw.besvarelse.domain.besvarelse.EndretAv
import no.nav.paw.besvarelse.domain.request.EndreSituasjonRequest
import no.nav.paw.besvarelse.domain.response.ArbeidssokerRegistrertResponse
import no.nav.paw.besvarelse.kafka.producer.ArbeidssokerBesvarelseProducer
import no.nav.paw.besvarelse.repository.ArbeidssokerRegistrertRepository

class ArbeidssokerRegistrertService(
    private val arbeidssokerRegistrertRepository: ArbeidssokerRegistrertRepository,
    private val arbeidssokerBesvarelseProducer: ArbeidssokerBesvarelseProducer,
    private val pdlService: PdlService
) {
    fun hentSiste(foedselsnummer: Foedselsnummer): ArbeidssokerRegistrertResponse {
        val identitetsListe = pdlService.hentIdenter(foedselsnummer.foedselsnummer)
        val bruker = Bruker(foedselsnummer, identitetsListe)

        return arbeidssokerRegistrertRepository.hentSiste(bruker).tilArbeidssokerRegistrertResponse()
    }

    fun opprettArbeidssokerRegistrert(arbeidssokerRegistrert: ArbeidssokerRegistrert) {
        val identitetsListe = pdlService.hentIdenter(arbeidssokerRegistrert.bruker.foedselsnummer.foedselsnummer)
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
        val identitetsListe = pdlService.hentIdenter(foedselsnummer.foedselsnummer)
        val bruker = Bruker(foedselsnummer, identitetsListe)

        return arbeidssokerRegistrertRepository.endreSituasjon(bruker, endreSituasjonRequest, endretAv).also {
            arbeidssokerBesvarelseProducer.publish(it.tilArbeidssokerBesvarelseEvent())
        }.tilArbeidssokerRegistrertResponse()
    }
}
