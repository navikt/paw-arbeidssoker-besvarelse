package no.nav.paw.besvarelse.services

import no.nav.paw.besvarelse.domain.ArbeidssokerRegistrert
import no.nav.paw.besvarelse.domain.Foedselsnummer
import no.nav.paw.besvarelse.domain.besvarelse.EndretAv
import no.nav.paw.besvarelse.domain.request.EndreSituasjonRequest
import no.nav.paw.besvarelse.domain.response.ArbeidssokerRegistrertResponse
import no.nav.paw.besvarelse.kafka.producer.ArbeidssokerBesvarelseEndretProducer
import no.nav.paw.besvarelse.repository.ArbeidssokerRegistrertRepository

class ArbeidssokerRegistrertService(
    private val arbeidssokerRegistrertRepository: ArbeidssokerRegistrertRepository,
    private val arbeidssokerBesvarelseEndretProducer: ArbeidssokerBesvarelseEndretProducer
) {
    fun hentSiste(foedselsnummer: Foedselsnummer): ArbeidssokerRegistrertResponse =
        arbeidssokerRegistrertRepository.hentSiste(foedselsnummer).tilArbeidssokerRegistrertResponse()

    fun opprettArbeidssokerRegistrert(arbeidssokerRegistrert: ArbeidssokerRegistrert): Long =
        arbeidssokerRegistrertRepository.opprett(arbeidssokerRegistrert.tilArbeidssokerRegistrertEntity())

    fun endreSituasjon(
        foedselsnummer: Foedselsnummer,
        endreSituasjonRequest: EndreSituasjonRequest,
        endretAv: EndretAv
    ): ArbeidssokerRegistrertResponse =
        arbeidssokerRegistrertRepository.endreSituasjon(foedselsnummer, endreSituasjonRequest, endretAv).also {
            // TODO: Må endre ArbeidssokerBesvarelseEndretEvent.avsc til å samsvare med ny struktur for svar
            // arbeidssokerBesvarelseEndretProducer.publish(it.tilArbeidssokerBesvarelseEndretEvent())
        }.tilArbeidssokerRegistrertResponse()
}
