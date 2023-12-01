package no.nav.paw.besvarelse.config

import io.github.cdimascio.dotenv.dotenv
import no.nav.security.token.support.v2.RequiredClaims

val dotenv = dotenv { ignoreIfMissing = true }

data class Config(
    val database: DatabaseConfig = DatabaseConfig(
        dotenv["NAIS_DATABASE_PAW_ARBEIDSSOKER_BESVARELSE_ARBEIDSSOKER_REGISTRERT_HOST"],
        dotenv["NAIS_DATABASE_PAW_ARBEIDSSOKER_BESVARELSE_ARBEIDSSOKER_REGISTRERT_PORT"],
        dotenv["NAIS_DATABASE_PAW_ARBEIDSSOKER_BESVARELSE_ARBEIDSSOKER_REGISTRERT_DATABASE"],
        dotenv["NAIS_DATABASE_PAW_ARBEIDSSOKER_BESVARELSE_ARBEIDSSOKER_REGISTRERT_USERNAME"],
        dotenv["NAIS_DATABASE_PAW_ARBEIDSSOKER_BESVARELSE_ARBEIDSSOKER_REGISTRERT_PASSWORD"]
    ),
    val naisEnv: NaisEnv = NaisEnv.current(),
    val authentication: List<AuthProvider> = listOf(
        AuthProvider(
            name = "tokenx",
            discoveryUrl = dotenv["TOKEN_X_WELL_KNOWN_URL"],
            acceptedAudience = listOf(dotenv["TOKEN_X_CLIENT_ID"]),
            requiredClaims = RequiredClaims("tokenx", arrayOf("acr=Level4", "acr=idporten-loa-high"), true)

        ),
        AuthProvider(
            name = "azure",
            discoveryUrl = dotenv["AZURE_APP_WELL_KNOWN_URL"],
            acceptedAudience = listOf(dotenv["AZURE_APP_CLIENT_ID"]),
            requiredClaims = RequiredClaims("azure", arrayOf("NAVident"))
        )
    ),
    val kafka: KafkaConfig = KafkaConfig(
        dotenv["KAFKA_BROKER_URL"],
        dotenv["KAFKA_CONSUMER_GROUP_ID"],
        dotenv["KAFKA_PRODUCER_ID"],
        dotenv["KAFKA_SCHEMA_REGISTRY"],
        "${dotenv["KAFKA_SCHEMA_REGISTRY_USER"]}:${dotenv["KAFKA_SCHEMA_REGISTRY_PASSWORD"]}",
        KafkaProducers(
            KafkaProducer(
                dotenv["KAFKA_PRODUCER_ARBEIDSSOKER_BESVARELSE_TOPIC"]
            )
        ),
        KafkaConsumers(
            KafkaConsumer(
                dotenv["KAFKA_CONSUMER_ARBEIDSSOKER_REGISTERING_TOPIC"]
            )
        )
    ),
    val pdlClientConfig: ServiceClientConfig = ServiceClientConfig(
        dotenv["PDL_URL"],
        dotenv["PDL_SCOPE"]
    ),
    val poaoTilgangClient: ServiceClientConfig = ServiceClientConfig(
        dotenv["POAO_TILGANG_CLIENT_URL"],
        dotenv["POAO_TILGANG_CLIENT_SCOPE"]
    )
)

data class DatabaseConfig(
    val host: String,
    val port: String,
    val database: String,
    val username: String,
    val password: String
) {
    val jdbcUrl: String get() = "jdbc:postgresql://$host:$port/$database?user=$username&password=$password"
}

data class KafkaConfig(
    val brokerUrl: String? = null,
    val consumerGroupId: String,
    val producerId: String,
    val schemaRegisteryUrl: String?,
    val schemaRegisteryUserInfo: String?,
    val producers: KafkaProducers,
    val consumers: KafkaConsumers
)

data class KafkaConsumers(
    val arbeidssokerRegistrering: KafkaConsumer
)

data class KafkaConsumer(
    val topic: String
)

data class KafkaProducers(
    val arbeidssokerBesvarelse: KafkaProducer
)

data class KafkaProducer(
    val topic: String
)
data class AuthProvider(
    val name: String,
    val discoveryUrl: String,
    val acceptedAudience: List<String>,
    val cookieName: String? = null,
    val requiredClaims: RequiredClaims? = null
)

data class ServiceClientConfig(
    val url: String,
    val scope: String
)

enum class NaisEnv(val clusterName: String) {
    Local("local"),
    DevGCP("dev-gcp"),
    ProdGCP("prod-gcp");

    companion object {
        fun current(): NaisEnv = when (System.getenv("NAIS_CLUSTER_NAME")) {
            DevGCP.clusterName -> DevGCP
            ProdGCP.clusterName -> ProdGCP
            else -> Local
        }
    }
}
