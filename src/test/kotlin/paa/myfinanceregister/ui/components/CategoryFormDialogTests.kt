package paa.myfinanceregister.ui.components

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.provider.Query
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

        val descriptionField = components.filterIsInstance<TextField>().single()
        assertThat(descriptionField.value).isEqualTo("Food")
        assertThat(descriptionField.maxLength).isEqualTo(Category.MAX_DESCRIPTION_LENGTH)
        assertThat(descriptionField.maxWidth).isEqualTo("24rem")
        assertThat(rootField.value).isSameAs(root)
        assertThat(components.filterIsInstance<Button>().map { it.text }).containsExactly("Cancel", "Save")
        assertThat(rootField.helperText).isEqualTo("Leave empty for a top-level category.")
    }

    @Test
    fun editDialogExcludesCategoryAndItsDescendantsFromRootOptions() {
        val expenses = Category(1L, "Expenses")
        val food = Category(2L, "Food", expenses)
        val groceries = Category(3L, "Groceries", food)
        val dialog = CategoryFormDialog(listOf(expenses, food, groceries), expenses) { _, _, _ -> true }
        val rootField = descendants(dialog).filterIsInstance<ComboBox<Category>>().single()
        val availableRoots = rootField.dataProvider.fetch(Query()).toList()

        assertThat(availableRoots).isEmpty()
    }

    private fun descendants(component: Component): List<Component> =
        listOf(component) + component.children.toList().flatMap(::descendants)
}
