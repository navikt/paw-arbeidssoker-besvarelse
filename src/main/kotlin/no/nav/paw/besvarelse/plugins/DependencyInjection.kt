package no.nav.paw.besvarelse.plugins

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClientConfig
import io.confluent.kafka.serializers.KafkaAvroSerializer
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import no.nav.common.kafka.producer.util.KafkaProducerClientBuilder
import no.nav.common.kafka.util.KafkaPropertiesBuilder
import no.nav.common.kafka.util.KafkaPropertiesPreset
import no.nav.paw.besvarelse.config.Config
import no.nav.paw.besvarelse.config.NaisEnv
import no.nav.paw.besvarelse.config.createDatabaseConfig
import no.nav.paw.besvarelse.kafka.consumer.ArbeidssokerRegistreringConsumer
import no.nav.paw.besvarelse.kafka.producer.ArbeidssokerBesvarelseProducer
import no.nav.paw.besvarelse.repository.ArbeidssokerRegistrertRepository
import no.nav.paw.besvarelse.services.ArbeidssokerRegistrertService
import no.nav.paw.besvarelse.services.AutorisasjonService
import no.nav.paw.besvarelse.services.PdlService
import no.nav.paw.besvarelse.token.TokenService
import no.nav.paw.pdl.PdlClient
import no.nav.poao_tilgang.client.PoaoTilgangCachedClient
import no.nav.poao_tilgang.client.PoaoTilgangHttpClient
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun Application.configureDependencyInjection(config: Config) {
    install(Koin) {
        modules(
            module {
                single { createDatabaseConfig(config.database.jdbcUrl) }

                single {
                    HttpClient {
                        install(ContentNegotiation) {
                            jackson()
                        }
                    }
                }

                single {
                    val producerProperties = when (NaisEnv.current()) {
                        NaisEnv.Local -> KafkaPropertiesBuilder.producerBuilder()
                            .withBaseProperties()
                            .withProducerId(config.kafka.producerId)
                            .withBrokerUrl(config.kafka.brokerUrl)
                            .withSerializers(StringSerializer::class.java, KafkaAvroSerializer::class.java)
                            .withProp(
                                KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG,
                                config.kafka.schemaRegisteryUrl
                            )
                            .build()

                        else -> KafkaPropertiesBuilder.producerBuilder()
                            .withProps(KafkaPropertiesPreset.aivenDefaultProducerProperties(config.kafka.producerId))
                            .withSerializers(StringSerializer::class.java, KafkaAvroSerializer::class.java)
                            .withProp(
                                KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG,
                                config.kafka.schemaRegisteryUrl
                            )
                            .withProp(SchemaRegistryClientConfig.BASIC_AUTH_CREDENTIALS_SOURCE, "USER_INFO")
                            .withProp(SchemaRegistryClientConfig.USER_INFO_CONFIG, config.kafka.schemaRegisteryUserInfo)
                            .build()
                    }

                    val client = KafkaProducerClientBuilder.builder<String, String>()
                        .withProperties(producerProperties)
                        .build()
                    client
                }

                single {
                    val consumerProperties = when (NaisEnv.current()) {
                        NaisEnv.Local -> KafkaPropertiesBuilder.consumerBuilder()
                            .withBaseProperties()
                            .withConsumerGroupId(config.kafka.consumerGroupId)
                            .withBrokerUrl(config.kafka.brokerUrl)
                            .withDeserializers(StringDeserializer::class.java, StringDeserializer::class.java)
                            .build()

                        else -> KafkaPropertiesBuilder.consumerBuilder()
                            .withProps(KafkaPropertiesPreset.aivenDefaultConsumerProperties(config.kafka.consumerGroupId))
                            .withDeserializers(StringDeserializer::class.java, StringDeserializer::class.java)
                            .build()
                    }

                    KafkaConsumer<String, String>(consumerProperties)
                }

                single {
                    jacksonObjectMapper().apply {
                        registerKotlinModule()
                        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                        registerModule(JavaTimeModule())
                    }
                }

                single {
                    PoaoTilgangCachedClient(
                        PoaoTilgangHttpClient(
                            config.poaoTilgangClient.url,
                            { TokenService().createMachineToMachineToken(config.poaoTilgangClient.scope) }
                        )
                    )
                }

                single { AutorisasjonService(get()) }

                single { PdlClient(config.pdlClientConfig.url, "OPP", get()) { TokenService().createMachineToMachineToken(config.pdlClientConfig.scope) } }

                single { PdlService(get()) }

                single {
                    ArbeidssokerRegistreringConsumer(
                        config.kafka.consumers.arbeidssokerRegistrering.topic,
                        get(),
                        get(),
                        get(),
                    )
                }
                single { ArbeidssokerBesvarelseProducer(get(), config.kafka.producers.arbeidssokerBesvarelse.topic) }
                single { ArbeidssokerRegistrertRepository(get(), get()) }
                single { ArbeidssokerRegistrertService(get(), get(), get()) }
            }
        )
    }
}
