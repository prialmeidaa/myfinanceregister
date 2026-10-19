package paa.myfinanceregister

import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import jakarta.annotation.security.PermitAll
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.mock.env.MockEnvironment
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import paa.myfinanceregister.ui.views.LoginView
import paa.myfinanceregister.ui.views.MainView

@SpringBootTest
@AutoConfigureMockMvc
class MyFinanceRegisterApplicationTests {
    @Autowired
    private lateinit var clientRegistrationRepository: ClientRegistrationRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun contextLoads() {
    }

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
    fun loginViewIsPublicAndLinksToGoogleAuthorization() {
        val loginView = LoginView(MockEnvironment())
        val route = LoginView::class.java.getAnnotation(Route::class.java)

        assertThat(route.value).isEqualTo("login")
        assertThat(LoginView::class.java.isAnnotationPresent(AnonymousAllowed::class.java)).isTrue()
        val googleLink = loginView.children.toList().filterIsInstance<Anchor>().single()
        assertThat(googleLink.href).isEqualTo("/oauth2/authorization/google")
    }

    @Test
    fun mainViewRemainsConfiguredOnRootRoute() {
        val route = MainView::class.java.getAnnotation(Route::class.java)

        assertThat(route.value).isEmpty()
        assertThat(MainView::class.java.isAnnotationPresent(PermitAll::class.java)).isTrue()
    }
}
