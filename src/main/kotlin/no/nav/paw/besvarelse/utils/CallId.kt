package no.nav.paw.besvarelse.utils

import org.slf4j.MDC
import java.util.*

object CallId {
    val callId: String get() = MDC.get("x_callId") ?: UUID.randomUUID().toString()

    fun leggTilCallId() = MDC.put("x_callId", UUID.randomUUID().toString())
}
