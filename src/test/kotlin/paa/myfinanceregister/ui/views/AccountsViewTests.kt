package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import paa.myfinanceregister.ui.layout.MainLayout

class AccountsViewTests {
    @Test
    fun accountsViewUsesSharedLayoutAndShowsTodo() {
        val view = AccountsView()
        val route = AccountsView::class.java.getAnnotation(Route::class.java)
        val todo = view.children.toList().filterIsInstance<Paragraph>().single()

        assertThat(route.value).isEqualTo("accounts")
        assertThat(route.layout.java).isEqualTo(MainLayout::class.java)
        assertThat(AccountsView::class.java.isAnnotationPresent(PermitAll::class.java)).isTrue()
        assertThat(todo.text).isEqualTo("TODO: Accounts")
    }
}
