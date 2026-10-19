package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.login.LoginForm
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import org.springframework.core.env.Environment

@Route("login")
@PageTitle("Login")
@AnonymousAllowed
class LoginView(private val environment: Environment) : VerticalLayout() {
    init {
        if (environment.matchesProfiles("dev")) {
            val loginForm = LoginForm()
            loginForm.action = "login"
            add(loginForm)
        } else {
            add(
                H1("My Finance Register"),
                Anchor("/oauth2/authorization/google", "Login with Google")
            )
        }
    }
}
