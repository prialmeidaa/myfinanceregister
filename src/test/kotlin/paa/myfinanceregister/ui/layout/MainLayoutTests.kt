package paa.myfinanceregister.ui.layout

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.Hr
import com.vaadin.flow.component.html.Image
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
            .containsExactly("My Finance Register")
        assertThat(components.filterIsInstance<Image>().map { it.src })
            .containsExactly("/images/logo-192x192.png")
        val logo = components.filterIsInstance<Image>().single()
        assertThat(logo.width).isEqualTo("100px")
        assertThat(logo.height).isEqualTo("100px")
        assertThat(logo.element.style.get("display")).isEqualTo("block")
        assertThat(logo.element.style.get("margin")).isEqualTo("0 auto")
        assertThat(components.filterIsInstance<Hr>())
            .singleElement()
            .extracting { it.element.style.get("margin-inline") }
            .isEqualTo("var(--lumo-space-m)")
        assertThat(components.filterIsInstance<MainMenu>()).hasSize(1)
        assertThat(MainLayout::class.java.isAnnotationPresent(AnonymousAllowed::class.java)).isTrue()
    }

    @Test
    fun mainLayoutUpdatesNavbarTitleWhenPageTitleChanges() {
        val layout = MainLayout()

        layout.updateCurrentPageTitle("Accounts")

        assertThat(descendants(layout).filterIsInstance<H1>().single().text).isEqualTo("Accounts")
    }

    private fun descendants(component: Component): List<Component> =
        listOf(component) + component.children.toList().flatMap(::descendants)
}
