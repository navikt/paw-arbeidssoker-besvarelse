package no.nav.paw.besvarelse.kafka.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.featuretoggle.UnleashClient
import no.nav.paw.besvarelse.domain.ArbeidssokerRegistrert
import no.nav.paw.besvarelse.domain.kafka.ArbeidssokerRegistrertFraKafkaMelding
import no.nav.paw.besvarelse.services.ArbeidssokerRegistrertService
import no.nav.paw.besvarelse.utils.CallId.leggTilCallId
import no.nav.paw.besvarelse.utils.logger
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.time.Duration

class ArbeidssokerRegistreringConsumer(
    private val topic: String,
    private val consumer: KafkaConsumer<String, String>,
    private val arbeidssokerRegistrertService: ArbeidssokerRegistrertService,
    private val unleashClient: UnleashClient,
    private val objectMapper: ObjectMapper
) {

    fun start() {
        logger.info("Lytter på topic $topic")
        consumer.subscribe(listOf(topic))

        while (true) {
            consumer.poll(Duration.ofMillis(500)).forEach { post ->
                try {
                    leggTilCallId()
                    val arbeidssokerRegistrert: ArbeidssokerRegistrert = objectMapper.readValue<ArbeidssokerRegistrertFraKafkaMelding>(post.value())
                        .tilArbeidssokerRegistrert()

                    logger.info("Mottok melding fra $topic med offset ${post.offset()}p${post.partition()}")

                    arbeidssokerRegistrertService.opprettArbeidssokerRegistrert(arbeidssokerRegistrert)
                } catch (error: Exception) {
                    logger.error("Feil ved konsumering av melding fra $topic", error)
                    throw error
                }
                consumer.commitSync()
            }
        }
    }
}
