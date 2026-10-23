package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.html.Nav
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MainViewTests {
    @Test
    fun mainViewRemainsConfiguredOnRootRoute() {
        val mainView = MainView()
        val route = MainView::class.java.getAnnotation(Route::class.java)

        assertThat(route.value).isEmpty()
        assertThat(MainView::class.java.isAnnotationPresent(PermitAll::class.java)).isTrue()
        val navigation = mainView.children.toList().filterIsInstance<Nav>().single()
        val links = navigation.children.toList()
            .flatMap { it.children.toList() }
            .filterIsInstance<Anchor>()

        assertThat(links.map { it.text }).containsExactly("Home", "Accounts", "Statement")
        assertThat(links.map { it.href }).containsExactly("/", "/accounts", "/statement")
    }
}
