package no.nav.paw.besvarelse

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import no.nav.paw.besvarelse.config.Config
import no.nav.paw.besvarelse.config.migrateDatabase
import no.nav.paw.besvarelse.kafka.consumer.ArbeidssokerRegistreringConsumer
import no.nav.paw.besvarelse.plugins.configureAuthentication
import no.nav.paw.besvarelse.plugins.configureDependencyInjection
import no.nav.paw.besvarelse.plugins.configureHTTP
import no.nav.paw.besvarelse.plugins.configureLogging
import no.nav.paw.besvarelse.plugins.configureSerialization
import no.nav.paw.besvarelse.routes.apiRoutes
import no.nav.paw.besvarelse.routes.internalRoutes
import no.nav.paw.besvarelse.routes.swaggerRoutes
import org.koin.ktor.ext.inject
import javax.sql.DataSource
import kotlin.concurrent.thread

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val config = Config()

    // Plugins
    configureDependencyInjection(config)
    configureAuthentication(config.authentication)
    configureHTTP()
    configureLogging()
    configureSerialization()

    // Migrate database
    val dataSource by inject<DataSource>()
    migrateDatabase(dataSource)

    val arbeidssokerRegistreringConsumer by inject<ArbeidssokerRegistreringConsumer>()

    thread {
        arbeidssokerRegistreringConsumer.start()
    }

    // Routes
    routing {
        internalRoutes()
        swaggerRoutes()
        apiRoutes()
    }
}
