package paa.myfinanceregister.security

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SecurityConfigurationTests {
    @LocalServerPort
    private var port = 0

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

    @Test
    fun logoImageIsAvailableWithoutAuthentication() {
        val request = HttpRequest.newBuilder(
            URI.create("http://localhost:$port/images/logo-192x192.png")
        ).GET().build()
        val response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofByteArray())

        assertThat(response.statusCode()).isEqualTo(200)
        assertThat(response.headers().firstValue("Content-Type").orElse("")).isEqualTo("image/png")
        assertThat(response.body()).isNotEmpty()
    }
}
