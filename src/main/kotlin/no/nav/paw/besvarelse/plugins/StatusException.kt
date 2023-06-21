package no.nav.paw.besvarelse.plugins

import io.ktor.http.HttpStatusCode

class StatusException(val status: HttpStatusCode, val description: String? = null) :
    Exception("Request failed with status: $status. Description: $description")

class BesvarelseNotFound :
    Exception("Besvarelsen ble ikke funnet")
