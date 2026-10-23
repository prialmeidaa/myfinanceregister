package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.login.LoginForm
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.mock.env.MockEnvironment
import paa.myfinanceregister.ui.layout.MainLayout

class LoginViewTests {
    @Test
    fun loginViewIsPublicAndLinksToGoogleAuthorization() {
        val loginView = LoginView(MockEnvironment())
        val route = LoginView::class.java.getAnnotation(Route::class.java)

        assertThat(route.value).isEqualTo("login")
        assertThat(route.layout.java).isEqualTo(MainLayout::class.java)
        assertThat(LoginView::class.java.isAnnotationPresent(AnonymousAllowed::class.java)).isTrue()
        val googleLink = loginView.children.toList().filterIsInstance<Anchor>().single()
        assertThat(googleLink.href).isEqualTo("/oauth2/authorization/google")
    }

    @Test
    fun developmentLoginUsesDefaultVaadinLoginPage() {
        val environment = MockEnvironment().apply { setActiveProfiles("dev") }
        val loginView = LoginView(environment)
        val route = LoginView::class.java.getAnnotation(Route::class.java)
        val loginForm = loginView.children.toList().filterIsInstance<LoginForm>().single()

        assertThat(route.value).isEqualTo("login")
        assertThat(route.layout.java).isEqualTo(MainLayout::class.java)
        assertThat(LoginView::class.java.isAnnotationPresent(AnonymousAllowed::class.java)).isTrue()
        assertThat(loginForm.action).isEqualTo("login")
    }
}
