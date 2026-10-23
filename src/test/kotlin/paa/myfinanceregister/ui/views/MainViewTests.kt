package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import paa.myfinanceregister.ui.layout.MainLayout

class MainViewTests {
    @Test
    fun mainViewUsesSharedLayoutAndShowsTodo() {
        val mainView = MainView()
        val route = MainView::class.java.getAnnotation(Route::class.java)
        val todo = mainView.children.toList().filterIsInstance<Paragraph>().single()

        assertThat(route.value).isEmpty()
        assertThat(route.layout.java).isEqualTo(MainLayout::class.java)
        assertThat(MainView::class.java.isAnnotationPresent(PermitAll::class.java)).isTrue()
        assertThat(todo.text).isEqualTo("TODO: Home")
    }
}
