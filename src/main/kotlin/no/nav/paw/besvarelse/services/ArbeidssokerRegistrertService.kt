package no.nav.paw.besvarelse.services

import no.nav.paw.besvarelse.domain.ArbeidssokerRegistrert
import no.nav.paw.besvarelse.domain.Foedselsnummer
import no.nav.paw.besvarelse.domain.besvarelse.EndretAv
import no.nav.paw.besvarelse.domain.request.EndreSituasjonRequest
import no.nav.paw.besvarelse.domain.response.ArbeidssokerRegistrertResponse
import no.nav.paw.besvarelse.kafka.producer.ArbeidssokerBesvarelseProducer
import no.nav.paw.besvarelse.repository.ArbeidssokerRegistrertRepository

class ArbeidssokerRegistrertService(
    private val arbeidssokerRegistrertRepository: ArbeidssokerRegistrertRepository,
    private val arbeidssokerBesvarelseProducer: ArbeidssokerBesvarelseProducer
) {
    fun hentSiste(foedselsnummer: Foedselsnummer): ArbeidssokerRegistrertResponse =
        arbeidssokerRegistrertRepository.hentSiste(foedselsnummer).tilArbeidssokerRegistrertResponse()

    fun opprettArbeidssokerRegistrert(arbeidssokerRegistrert: ArbeidssokerRegistrert) {
        arbeidssokerRegistrertRepository.opprett(arbeidssokerRegistrert.tilArbeidssokerRegistrertEntity()).also {
            arbeidssokerBesvarelseProducer.publish(it.tilArbeidssokerBesvarelseEvent())
        }
    }

    fun endreSituasjon(
        foedselsnummer: Foedselsnummer,
        endreSituasjonRequest: EndreSituasjonRequest,
        endretAv: EndretAv
    ): ArbeidssokerRegistrertResponse =
        arbeidssokerRegistrertRepository.endreSituasjon(foedselsnummer, endreSituasjonRequest, endretAv).also {
            // TODO: Må aktiveres når modell for besvarelseEndret er satt
            arbeidssokerBesvarelseProducer.publish(it.tilArbeidssokerBesvarelseEvent())
        }.tilArbeidssokerRegistrertResponse()
}
