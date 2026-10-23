package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import paa.myfinanceregister.ui.layout.MainLayout

class StatementViewTests {
    @Test
    fun statementViewUsesSharedLayoutAndShowsTodo() {
        val view = StatementView()
        val route = StatementView::class.java.getAnnotation(Route::class.java)
        val todo = view.children.toList().filterIsInstance<Paragraph>().single()

        assertThat(route.value).isEqualTo("statement")
        assertThat(route.layout.java).isEqualTo(MainLayout::class.java)
        assertThat(StatementView::class.java.isAnnotationPresent(PermitAll::class.java)).isTrue()
        assertThat(todo.text).isEqualTo("TODO: Statement")
    }
}
