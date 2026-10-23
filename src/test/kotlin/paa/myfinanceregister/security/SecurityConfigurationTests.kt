package paa.myfinanceregister.security

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigurationTests {
    @Autowired
    private lateinit var clientRegistrationRepository: ClientRegistrationRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun googleRegistrationUsesConfiguredOAuthCredentialsAndScopes() {
        val registration = requireNotNull(clientRegistrationRepository.findByRegistrationId("google"))

        assertThat(registration.clientId).isNotBlank()
        assertThat(registration.clientSecret).isNotBlank()
        assertThat(registration.scopes).containsExactlyInAnyOrder("openid", "profile", "email")
        assertThat(registration.redirectUri).isEqualTo("{baseUrl}/login/oauth2/code/{registrationId}")
    }

    @Test
    fun googleAuthorizationEndpointRedirectsToGoogle() {
        val response = mockMvc.perform(get("/oauth2/authorization/google")).andReturn().response

        assertThat(response.status).isEqualTo(302)
        assertThat(response.getHeader("Location")).contains("accounts.google.com")
    }
}
