package paa.myfinanceregister.security

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("dev")
class SecurityDevelopmentConfigurationTests {
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
}
