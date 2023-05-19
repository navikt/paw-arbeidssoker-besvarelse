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

fun ApplicationCall.getSubClaim(): UUID =
    getClaim("AAD", "sub")
        ?.let { UUID.fromString(it) }
        ?: throw StatusException(HttpStatusCode.Forbidden, "Fant ikke 'sub'-claim i token fra issuer")
