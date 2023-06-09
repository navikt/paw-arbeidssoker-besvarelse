package no.nav.paw.besvarelse.token

import no.nav.common.token_client.builder.AzureAdTokenClientBuilder
import no.nav.paw.besvarelse.config.NaisEnv
import no.nav.paw.besvarelse.config.dotenv
import no.nav.paw.besvarelse.utils.createMockRSAKey
import no.nav.paw.besvarelse.utils.logger

class TokenService {
    fun createMachineToMachineToken(scope: String): String {
        logger.info("Lager nytt Azure AD M2M-token")
        return aadMachineToMachineTokenClient.createMachineToMachineToken(scope)
    }

    private val aadMachineToMachineTokenClient = when (NaisEnv.current()) {
        NaisEnv.Local -> AzureAdTokenClientBuilder.builder()
            .withClientId(dotenv["AZURE_APP_CLIENT_ID"])
            .withPrivateJwk(createMockRSAKey("azure"))
            .withTokenEndpointUrl(dotenv["AZURE_OPENID_CONFIG_TOKEN_ENDPOINT"])
            .buildMachineToMachineTokenClient()

        else -> AzureAdTokenClientBuilder.builder()
            .withNaisDefaults()
            .buildMachineToMachineTokenClient()
    }
}
