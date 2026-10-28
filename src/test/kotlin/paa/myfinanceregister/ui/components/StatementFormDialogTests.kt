package paa.myfinanceregister.ui.components

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.textfield.BigDecimalField
import com.vaadin.flow.component.textfield.TextField
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import paa.myfinanceregister.domain.model.Account
import paa.myfinanceregister.domain.model.Category
import paa.myfinanceregister.domain.model.Statement
import paa.myfinanceregister.services.StatementFormData
import java.math.BigDecimal
import java.time.LocalDate

class StatementFormDialogTests {
    @Test
    fun editDialogSelectsRelatedItemsByTheirIds() {
        val selectedCategory = Category(1, "Food")
        val selectedAccount = Account(2, "Checking")
        val statement = Statement(
            id = 5,
            date = LocalDate.of(2026, 10, 28),
            description = "Lunch",
            category = Category(1, "Food"),
            account = Account(2, "Checking")
        )
        val dialog = StatementFormDialog(
            listOf(selectedCategory),
            listOf(selectedAccount),
            statement
        ) { _, _ -> false }
        val components = descendants(dialog)

        assertThat(components.filterIsInstance<ComboBox<Category>>().first().value)
            .isSameAs(selectedCategory)
        assertThat(components.filterIsInstance<ComboBox<Account>>().last().value)
            .isSameAs(selectedAccount)
    }

    @Test
    fun saveSubmitsTheSelectedStatementValues() {
        val category = Category(1, "Food")
        val account = Account(2, "Checking")
        var submittedId: Long? = null
        var submittedData: StatementFormData? = null
        val dialog = StatementFormDialog(listOf(category), listOf(account), null) { id, data ->
            submittedId = id
            submittedData = data
            false
        }
        val components = descendants(dialog)
        components.filterIsInstance<DatePicker>().single().value = LocalDate.of(2026, 10, 28)
        components.filterIsInstance<TextField>().single().value = "Lunch"
        components.filterIsInstance<ComboBox<Category>>().first().value = category
        components.filterIsInstance<ComboBox<Account>>().last().value = account
        components.filterIsInstance<BigDecimalField>().single().value = BigDecimal("18.25")

        components.filterIsInstance<Button>().single { it.text == "Save" }.click()

        assertThat(submittedId).isNull()
        assertThat(submittedData?.date).isEqualTo(LocalDate.of(2026, 10, 28))
        assertThat(submittedData?.description).isEqualTo("Lunch")
        assertThat(submittedData?.categoryId).isEqualTo(1)
        assertThat(submittedData?.accountId).isEqualTo(2)
        assertThat(submittedData?.amount).isEqualByComparingTo("18.25")
    }

    private fun descendants(component: Component): List<Component> =
        listOf(component) + component.children.toList().flatMap(::descendants)
}
