package no.nav.paw.besvarelse.kafka.producer

import no.nav.common.kafka.producer.KafkaProducerClient
import no.nav.paw.besvarelse.ArbeidssokerBesvarelseEvent
import no.nav.paw.besvarelse.utils.CallId.callId
import no.nav.paw.besvarelse.utils.logger
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import java.nio.charset.StandardCharsets
import java.util.*

class ArbeidssokerBesvarelseProducer(
    private val kafkaProducerClient: KafkaProducerClient<String, ArbeidssokerBesvarelseEvent>,
    private val topic: String
) {
    fun publish(value: ArbeidssokerBesvarelseEvent) {
        val record: ProducerRecord<String, ArbeidssokerBesvarelseEvent> = ProducerRecord(
            topic,
            null,
            UUID.randomUUID().toString(),
            value,
            listOf(RecordHeader("CallId", callId.toByteArray(StandardCharsets.UTF_8)))
        )

        kafkaProducerClient.sendSync(record)
        logger.info("Sendte melding om situasjonsendring til $topic")
    }
}
