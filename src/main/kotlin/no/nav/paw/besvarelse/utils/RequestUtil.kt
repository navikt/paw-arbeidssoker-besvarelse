package no.nav.paw.besvarelse.utils

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authentication
import no.nav.paw.besvarelse.domain.Foedselsnummer
import no.nav.paw.besvarelse.plugins.StatusException
import no.nav.security.token.support.v2.TokenValidationContextPrincipal
import java.util.*

fun ApplicationCall.getClaim(issuer: String, name: String): String? =
    authentication.principal<TokenValidationContextPrincipal>()
        ?.context
        ?.getClaims(issuer)
        ?.getStringClaim(name)

fun ApplicationCall.getPidClaim(): Foedselsnummer =
    getClaim("idporten", "pid")
        ?.let { Foedselsnummer(it) }
        ?: getClaim("tokendings", "pid")
            ?.let { Foedselsnummer(it) }
        ?: throw StatusException(HttpStatusCode.Forbidden, "Fant ikke 'pid'-claim i token fra issuer")

fun ApplicationCall.getNavAnsattAzureId(): UUID =
    getClaim("azure", "oid")
        ?.let { UUID.fromString(it) }
        ?: throw StatusException(HttpStatusCode.Forbidden, "Fant ikke 'oid'-claim i token fra issuer")

fun ApplicationCall.getNAVident(): String =
    getClaim("azure", "NAVident")
        ?: throw StatusException(HttpStatusCode.Forbidden, "Fant ikke 'NAVident'-claim i token fra issuer")
