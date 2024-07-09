import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

val githubPassword: String by project
val jvmMajorVersion: String by project

val ktor_version: String by project
val logback_version: String by project
val koin_version: String by project
val hikaricp_version: String by project
val postgresql_version: String by project
val flyway_version: String by project
val kotliquery_version: String by project
val jackson_version: String by project
val logstash_version: String by project
val testcontainers_version: String by project
val testcontainers_postgres_version: String by project
val prometheus_version: String by project
val nav_common_modules_version: String by project
val token_support_version: String by project
val aareg_client_version: String by project
val junit_jupiter_version: String by project
val dotenv_kotlin_version: String by project
val nocommons_version: String by project
val mock_oauth2_server_version: String by project
val avro_version: String by project
val pdl_client_version: String by project
val poao_tilgang_version: String by project
val micrometer_version: String by project
val otel_javaagent_version: String by project

val javaVersion = JavaLanguageVersion.of(jvmMajorVersion)
val baseImage: String by project
val image: String? by project

val agents by configurations.creating

plugins {
    kotlin("jvm") version "2.0.0"
    id("io.ktor.plugin") version "2.3.12"
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1" // TODO Plugin er ikke lengre under aktiv utvikling
    id("com.google.cloud.tools.jib") version "3.4.1"
}

group = "no.nav.paw.besvarelse"
version = "0.0.1"

repositories {
    mavenCentral()
    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
    maven {
        url = uri("https://jitpack.io")
    }
    maven {
        url = uri("https://maven.pkg.github.com/navikt/common-java-modules")
        credentials {
            username = "x-access-token"
            password = githubPassword
        }
    }
    maven {
        url = uri("https://maven.pkg.github.com/navikt/token-support")
        credentials {
            username = "x-access-token"
            password = githubPassword
        }
    }
    maven {
        url = uri("https://maven.pkg.github.com/navikt/poao-tilgang")
        credentials {
            username = "x-access-token"
            password = githubPassword
        }
    }
    maven {
        url = uri("https://maven.pkg.github.com/navikt/paw-kotlin-clients")
        credentials {
            username = "x-access-token"
            password = githubPassword
        }
    }
}

dependencies {
    // NAV common
    implementation("no.nav.common:token-client:$nav_common_modules_version")
    implementation("no.nav.common:kafka:$nav_common_modules_version")
    implementation("no.nav.common:audit-log:$nav_common_modules_version")

    // NAV security
    implementation("no.nav.security:token-validation-ktor-v2:$token_support_version")
    implementation("no.nav.security:token-client-core:$token_support_version")

    // NAV POAO
    implementation("no.nav.poao-tilgang:client:$poao_tilgang_version")

    // NAV PAW
    implementation("no.nav.paw:pdl-client:$pdl_client_version")

    // Annet
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jackson_version")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jackson_version")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:$jackson_version")
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("org.apache.avro:avro:$avro_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstash_version")
    implementation("com.zaxxer:HikariCP:$hikaricp_version")
    implementation("org.postgresql:postgresql:$postgresql_version")
    implementation("org.flywaydb:flyway-core:$flyway_version")
    implementation("com.github.seratch:kotliquery:$kotliquery_version")
    implementation("io.micrometer:micrometer-registry-prometheus:$micrometer_version")
    implementation("no.bekk.bekkopen:nocommons:$nocommons_version")
    implementation("io.github.cdimascio:dotenv-kotlin:$dotenv_kotlin_version")

    // Ktor
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-jackson-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-call-id:$ktor_version")
    implementation("io.ktor:ktor-server-status-pages:$ktor_version")
    implementation("io.ktor:ktor-client-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-cio-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-jackson:$ktor_version")
    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("io.ktor:ktor-server-swagger:$ktor_version")
    implementation("io.ktor:ktor-server-metrics-micrometer:$ktor_version")
    implementation("io.ktor:ktor-server-metrics-micrometer-jvm:$ktor_version")

    // Test
    testImplementation(kotlin("test"))
    testImplementation("org.testcontainers:testcontainers:$testcontainers_version")
    testImplementation("org.testcontainers:postgresql:$testcontainers_version")
    testImplementation("org.testcontainers:kafka:$testcontainers_version")
    testImplementation("no.nav.security:mock-oauth2-server:$mock_oauth2_server_version")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")

    agents("io.opentelemetry.javaagent:opentelemetry-javaagent:$otel_javaagent_version")
}

application {
    mainClass.set("no.nav.paw.besvarelse.ApplicationKt")
}

kotlin {
    jvmToolchain {
        languageVersion.set(javaVersion)
    }
}

ktor {
    fatJar {
        archiveFileName.set("fat.jar")
    }
}

tasks.compileTestKotlin {
    dependsOn("generateTestAvroJava")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        showExceptions = true
        showStackTraces = true
        exceptionFormat = TestExceptionFormat.FULL
        events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        showStandardStreams = true
    }
}

task<JavaExec>("importerCsv") {
    mainClass.set("no.nav.paw.besvarelse.utils.CsvToArbeidssokerRegistrertKt")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<Copy>("copyAgents") {
    from(agents)
    into("${layout.buildDirectory.get()}/agents")
}

tasks.named("assemble") {
    finalizedBy("copyAgents")
}

tasks.withType(Jar::class) {
    manifest {
        attributes["Implementation-Version"] = project.version
        attributes["Main-Class"] = application.mainClass.get()
        attributes["Implementation-Title"] = rootProject.name
    }
}
