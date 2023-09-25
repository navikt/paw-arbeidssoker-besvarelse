package no.nav.paw.besvarelse.utils

import no.nav.common.audit_log.cef.CefMessage
import no.nav.common.audit_log.cef.CefMessageEvent
import no.nav.common.audit_log.cef.CefMessageSeverity
import no.nav.paw.besvarelse.config.dotenv
import no.nav.paw.besvarelse.domain.Foedselsnummer
import no.nav.paw.besvarelse.domain.NavAnsatt
import org.slf4j.LoggerFactory

inline val <reified T : Any> T.logger get() = LoggerFactory.getLogger(T::class.java.name)
inline val secureLogger get() = LoggerFactory.getLogger("Tjenestekall")

inline val autitLogger get() = LoggerFactory.getLogger("AuditLogger")

fun auditLogMelding(foedselsnummer: Foedselsnummer, navAnsatt: NavAnsatt, melding: String): String =
    CefMessage.builder()
        .applicationName(dotenv["NAIS_APP_NAME"])
        .event(CefMessageEvent.ACCESS)
        .name("Sporingslogg")
        .severity(CefMessageSeverity.INFO)
        .sourceUserId(navAnsatt.azureId.toString())
        .destinationUserId(foedselsnummer.foedselsnummer)
        .timeEnded(System.currentTimeMillis())
        .extension("msg", melding)
        .build()
        .toString()
