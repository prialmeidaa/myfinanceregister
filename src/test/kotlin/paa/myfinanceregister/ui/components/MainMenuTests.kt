package paa.myfinanceregister.ui.components

import com.vaadin.flow.component.sidenav.SideNavItem
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MainMenuTests {
    @Test
    fun mainMenuContainsEveryApplicationRoute() {
        val menu = MainMenu()
        val items = menu.children.toList().filterIsInstance<SideNavItem>()

        assertThat(items.map { it.label }).containsExactly("Home", "Accounts", "Categories", "Statement")
        assertThat(items.map { it.path }).containsExactly("", "accounts", "categories", "statement")
    }
}
