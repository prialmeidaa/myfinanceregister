package paa.myfinanceregister.ui.components

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.textfield.TextField
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import paa.myfinanceregister.domain.model.Category

class CategoryFormDialogTests {
    @Test
    fun editDialogPrepopulatesCategoryAndOffersOtherCategoriesAsRoots() {
        val root = Category(1L, "Expenses")
        val category = Category(2L, "Food", root)
        val dialog = CategoryFormDialog(listOf(root, category), category) { _, _, _ -> true }
        val components = descendants(dialog)
        val rootField = components.filterIsInstance<ComboBox<*>>().single()

        assertThat(components.filterIsInstance<TextField>().single().value).isEqualTo("Food")
        assertThat(rootField.value).isSameAs(root)
        assertThat(components.filterIsInstance<Button>().map { it.text }).containsExactly("Cancel", "Save")
    }

    private fun descendants(component: Component): List<Component> =
        listOf(component) + component.children.toList().flatMap(::descendants)
}
