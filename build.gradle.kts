import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

val githubPassword: String by project

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

plugins {
    kotlin("jvm") version "1.8.10"
    id("io.ktor.plugin") version "2.3.4"
    id("com.github.davidmc24.gradle.plugin.avro") version "1.7.0"
}

group = "no.nav.paw.besvarelse"
version = "0.0.1"

application {
    mainClass.set("no.nav.paw.besvarelse.ApplicationKt")
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
    maven {
        url = uri("https://jitpack.io")
    }
    maven {
        url = uri("https://maven.pkg.github.com/navikt/*")
        credentials {
            username = "x-access-token"
            password = githubPassword
        }
    }
}

tasks {
    compileJava {
        targetCompatibility = JavaVersion.VERSION_17.toString()
        sourceCompatibility = JavaVersion.VERSION_17.toString()
    }
    compileTestKotlin {
        dependsOn("generateTestAvroJava")
    }
    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of("17"))
        }
    }
    test {
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
}

ktor {
    fatJar {
        archiveFileName.set("fat.jar")
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

    // Annet
    implementation("no.nav.paw:pdl-client:$pdl_client_version")
    implementation("com.github.navikt.poao-tilgang:client:$poao_tilgang_version")
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
    implementation("io.micrometer:micrometer-registry-prometheus:$prometheus_version")
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

    implementation("io.micrometer:micrometer-registry-prometheus:$micrometer_version")

    // Test
    testImplementation(kotlin("test"))
    testImplementation("org.testcontainers:testcontainers:$testcontainers_version")
    testImplementation("org.testcontainers:postgresql:$testcontainers_version")
    testImplementation("org.testcontainers:kafka:$testcontainers_version")
    testImplementation("no.nav.security:mock-oauth2-server:$mock_oauth2_server_version")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
}
