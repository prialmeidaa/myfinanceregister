package paa.myfinanceregister.ui.layout

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.server.auth.AnonymousAllowed
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import paa.myfinanceregister.ui.components.MainMenu

class MainLayoutTests {
    @Test
    fun mainLayoutProvidesNavbarAndSharedDrawerMenu() {
        val layout = MainLayout()
        val components = descendants(layout)

        assertThat(components.filterIsInstance<DrawerToggle>()).hasSize(1)
        assertThat(components.filterIsInstance<H1>().map { it.text })
            .containsOnly("My Finance Register")
        assertThat(components.filterIsInstance<MainMenu>()).hasSize(1)
        assertThat(MainLayout::class.java.isAnnotationPresent(AnonymousAllowed::class.java)).isTrue()
    }

    private fun descendants(component: Component): List<Component> =
        listOf(component) + component.children.toList().flatMap(::descendants)
}
