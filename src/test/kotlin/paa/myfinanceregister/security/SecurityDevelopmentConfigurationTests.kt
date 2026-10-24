package paa.myfinanceregister.security

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.ApplicationContext
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.test.context.ActiveProfiles
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class SecurityDevelopmentConfigurationTests {
    @LocalServerPort
    private var port = 0

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Autowired
    private lateinit var securityFilterChains: List<SecurityFilterChain>

    @Test
    fun developmentProfileLoadsDevelopmentSecurityWithoutGoogleOAuth() {
        assertThat(applicationContext.getBeansOfType(ClientRegistrationRepository::class.java)).isEmpty()
        assertThat(applicationContext.containsBean("securityDevelopmentConfiguration")).isTrue()
        assertThat(applicationContext.containsBean("securityConfiguration")).isFalse()
        assertThat(securityFilterChains.flatMap { it.filters })
            .noneMatch { it is OAuth2AuthorizationRequestRedirectFilter }
    }

    @Test
    fun logoImageIsAvailableWithoutAuthenticationInDevelopmentProfile() {
        val request = HttpRequest.newBuilder(
            URI.create("http://localhost:$port/images/logo-192x192.png")
        ).GET().build()
        val response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofByteArray())

        assertThat(response.statusCode()).isEqualTo(200)
        assertThat(response.headers().firstValue("Content-Type").orElse("")).isEqualTo("image/png")
        assertThat(response.body()).isNotEmpty()
    }
}
