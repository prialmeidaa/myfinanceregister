package paa.myfinanceregister.ui.components

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.BigDecimalField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import paa.myfinanceregister.domain.model.Account
import paa.myfinanceregister.domain.model.Category
import paa.myfinanceregister.domain.model.Statement
import paa.myfinanceregister.domain.model.StatementKind
import paa.myfinanceregister.domain.model.StatementTransactionType
import paa.myfinanceregister.services.StatementFormData
import java.math.BigDecimal
import java.time.LocalDate

class StatementInlineEditorTests {
    @Test
    fun descriptionFieldLimitsInputToOneHundredCharacters() {
        val editor = StatementInlineEditor(emptyList(), emptyList(), statement(), { _, _ -> false }, {})
        val descriptionField = descendants(editor).filterIsInstance<TextField>()
            .single { it.label == "Description" }

        assertThat(descriptionField.maxLength).isEqualTo(Statement.MAX_DESCRIPTION_LENGTH)
    }

    @Test
    fun descriptionFieldHasLimitedVisualWidth() {
        val editor = StatementInlineEditor(emptyList(), emptyList(), statement(), { _, _ -> false }, {})
        val descriptionField = descendants(editor).filterIsInstance<TextField>()
            .single { it.label == "Description" }

        assertThat(descriptionField.maxWidth).isEqualTo("24rem")
    }

    @Test
    fun inlineSelectionFieldsUseLimitedVisualWidth() {
        val editor = StatementInlineEditor(
            listOf(Category(1, "Food")),
            listOf(Account(2, "Checking")),
            statement(),
            { _, _ -> false },
            {}
        )
        val components = descendants(editor)

        assertThat(components.filterIsInstance<ComboBox<Category>>()
            .single { it.label == "Category" }.maxWidth)
            .isEqualTo("24rem")
        components.filterIsInstance<ComboBox<Account>>().forEach {
            assertThat(it.maxWidth).isEqualTo("24rem")
        }
    }

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
            StatementFormData(
                LocalDate.of(2026, 10, 28),
                "Lunch",
                1,
                2,
                BigDecimal("18.25"),
                kind = StatementKind.EXPENSE
            )
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
        val typeField = descendants(editor).filterIsInstance<RadioButtonGroup<StatementTransactionType>>()
            .single { it.label == "Schedule" }

        assertThat(typeField.value).isEqualTo(StatementTransactionType.SINGLE)
    }

    @Test
    fun inlineEditorGroupsFieldsIntoAlignedRows() {
        val editor = StatementInlineEditor(emptyList(), emptyList(), statement(), { _, _ -> false }, {})
        val rows = editor.children.toList().filterIsInstance<HorizontalLayout>()

        assertThat(editor.classNames).contains("statement-inline-editor")
        assertThat(rows).hasSize(4)
        assertThat(rows[0].classNames).contains("statement-inline-editor-primary-row")
        assertThat(rows[1].classNames).contains("statement-inline-editor-financial-row")
        assertThat(rows[2].classNames).contains("statement-inline-editor-schedule-row")
        assertThat(rows[3].classNames).contains("statement-inline-editor-actions")
        assertThat(rows[0].children.toList()).hasSize(5)
        assertThat(rows[2].children.toList()).hasSize(1)
    }

    @Test
    fun inlineEditorSubmitsTransferAccountsWithoutCategory() {
        val origin = Account(2, "Checking")
        val destination = Account(3, "Savings")
        var submittedData: StatementFormData? = null
        val editor = StatementInlineEditor(
            listOf(Category(1, "Food")),
            listOf(origin, destination),
            statement(),
            onSave = { _, data ->
                submittedData = data
                false
            },
            onCancel = {}
        )
        val components = descendants(editor)
        components.filterIsInstance<RadioButtonGroup<StatementKind>>()
            .single { it.label == "Transaction type" }.value = StatementKind.TRANSFER
        components.filterIsInstance<ComboBox<Account>>().single { it.label == "Origin account" }.value = origin
        components.filterIsInstance<ComboBox<Account>>().single { it.label == "Destination account" }.value = destination

        components.filterIsInstance<Button>().single { it.text == "Save" }.click()

        assertThat(submittedData?.kind).isEqualTo(StatementKind.TRANSFER)
        assertThat(submittedData?.categoryId).isNull()
        assertThat(submittedData?.accountId).isEqualTo(2)
        assertThat(submittedData?.destinationAccountId).isEqualTo(3)
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
