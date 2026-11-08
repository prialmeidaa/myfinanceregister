package paa.myfinanceregister.ui.components

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.textfield.BigDecimalField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import paa.myfinanceregister.domain.model.Account
import paa.myfinanceregister.domain.model.Category
import paa.myfinanceregister.domain.model.Statement
import paa.myfinanceregister.domain.model.StatementTransactionType
import paa.myfinanceregister.services.StatementFormData
import java.math.BigDecimal
import java.time.LocalDate

class StatementInlineEditorTests {
    @Test
    fun saveSubmitsTheStatementAndEnteredValues() {
        val category = Category(1, "Food")
        val account = Account(2, "Checking")
        var submittedId: Long? = null
        var submittedData: StatementFormData? = null
        val editor = StatementInlineEditor(
            listOf(category),
            listOf(account),
            statement(),
            onSave = { id, data ->
                submittedId = id
                submittedData = data
                false
            },
            onCancel = {}
        )
        val components = descendants(editor)
        assertThat(components.filterIsInstance<DatePicker>().single().value)
            .isEqualTo(LocalDate.of(2026, 10, 5))
        assertThat(components.filterIsInstance<TextField>().single { it.label == "Description" }.value)
            .isEqualTo("Purchase")
        components.filterIsInstance<DatePicker>().single().value = LocalDate.of(2026, 10, 28)
        components.filterIsInstance<TextField>().single { it.label == "Description" }.value = "Lunch"
        components.filterIsInstance<ComboBox<Category>>().single { it.label == "Category" }.value = category
        components.filterIsInstance<ComboBox<Account>>().single { it.label == "Account" }.value = account
        components.filterIsInstance<BigDecimalField>().single().value = BigDecimal("18.25")

        components.filterIsInstance<Button>().single { it.text == "Save" }.click()

        assertThat(submittedId).isEqualTo(5)
        assertThat(submittedData).isEqualTo(
            StatementFormData(LocalDate.of(2026, 10, 28), "Lunch", 1, 2, BigDecimal("18.25"))
        )
    }

    @Test
    fun successfulSaveClosesTheInlineEditor() {
        var closed = false
        val editor = StatementInlineEditor(
            emptyList(),
            emptyList(),
            statement(),
            onSave = { _, _ -> true },
            onCancel = { closed = true }
        )

        descendants(editor).filterIsInstance<Button>().single { it.text == "Save" }.click()

        assertThat(closed).isTrue()
    }

    @Test
    fun inlineEditorOffersTransactionTypeSelection() {
        val editor = StatementInlineEditor(emptyList(), emptyList(), statement(), { _, _ -> false }, {})
        val typeField = descendants(editor).filterIsInstance<RadioButtonGroup<StatementTransactionType>>().single()

        assertThat(typeField.value).isEqualTo(StatementTransactionType.SINGLE)
    }

    private fun statement() = Statement(
        id = 5,
        date = LocalDate.of(2026, 10, 5),
        description = "Purchase",
        category = Category(1, "Food"),
        account = Account(2, "Checking"),
        amount = BigDecimal("10.00")
    )

    private fun descendants(component: Component): List<Component> =
        listOf(component) + component.children.toList().flatMap(::descendants)
}
