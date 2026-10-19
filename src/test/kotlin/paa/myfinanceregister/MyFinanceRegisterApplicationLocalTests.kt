package paa.myfinanceregister

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.ApplicationContext
import org.springframework.mock.env.MockEnvironment
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated

import com.vaadin.flow.component.login.LoginForm
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import paa.myfinanceregister.security.DevelopmentUserDetailsService
import paa.myfinanceregister.ui.views.LoginView

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class MyFinanceRegisterApplicationLocalTests {
    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Autowired
    private lateinit var securityFilterChains: List<SecurityFilterChain>

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun developmentProfileLoadsDevelopmentSecurityWithoutGoogleOAuth() {
        assertThat(applicationContext.getBeansOfType(ClientRegistrationRepository::class.java)).isEmpty()
        assertThat(applicationContext.containsBean("securityDevelopmentConfiguration")).isTrue()
        assertThat(applicationContext.containsBean("securityConfiguration")).isFalse()
    }

    @Test
    fun developmentLoginUsesDefaultVaadinLoginPage() {
        val environment = MockEnvironment().apply { setActiveProfiles("dev") }
        val loginView = LoginView(environment)
        val route = LoginView::class.java.getAnnotation(Route::class.java)
        val loginForm = loginView.children.toList().filterIsInstance<LoginForm>().single()

        assertThat(route.value).isEqualTo("login")
        assertThat(LoginView::class.java.isAnnotationPresent(AnonymousAllowed::class.java)).isTrue()
        assertThat(loginForm.action).isEqualTo("login")
        assertThat(securityFilterChains.flatMap { it.filters })
            .noneMatch { it is OAuth2AuthorizationRequestRedirectFilter }
    }

    @Test
    fun developmentCredentialsAuthenticateSuccessfully() {
        mockMvc.perform(
            formLogin()
                .user(DevelopmentUserDetailsService.DEVELOPMENT_USERNAME)
                .password(DevelopmentUserDetailsService.DEVELOPMENT_PASSWORD)
        ).andExpect(authenticated().withUsername(DevelopmentUserDetailsService.DEVELOPMENT_USERNAME))
    }
}
