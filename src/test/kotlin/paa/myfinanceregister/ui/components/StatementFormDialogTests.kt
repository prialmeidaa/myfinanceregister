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
import paa.myfinanceregister.domain.model.StatementKind
import paa.myfinanceregister.services.StatementFormData
import java.math.BigDecimal
import java.time.LocalDate

class StatementFormDialogTests {
    @Test
    fun descriptionFieldLimitsInputToOneHundredCharacters() {
        val dialog = StatementFormDialog(emptyList(), emptyList(), null) { _, _ -> false }
        val descriptionField = descendants(dialog).filterIsInstance<TextField>().single()

        assertThat(descriptionField.maxLength).isEqualTo(Statement.MAX_DESCRIPTION_LENGTH)
        assertThat(descriptionField.maxWidth).isEqualTo("24rem")
    }

    @Test
    fun statementSelectionFieldsUseLimitedVisualWidth() {
        val dialog = StatementFormDialog(
            listOf(Category(1, "Food")),
            listOf(Account(2, "Checking"), Account(3, "Savings")),
            null
        ) { _, _ -> false }
        val components = descendants(dialog)

        assertThat(components.filterIsInstance<ComboBox<Category>>()
            .single { it.label == "Category" }.maxWidth)
            .isEqualTo("24rem")
        components.filterIsInstance<ComboBox<Account>>().forEach {
            assertThat(it.maxWidth).isEqualTo("24rem")
        }
    }

    @Test
    fun newDialogOnlyShowsActiveAccounts() {
        val activeAccount = Account(1, "Checking")
        val inactiveAccount = Account(2, "Old account", active = false)
        val dialog = StatementFormDialog(
            listOf(Category(1, "Food")),
            listOf(activeAccount, inactiveAccount),
            null
        ) { _, _ -> false }
        val accountField = descendants(dialog)
            .filterIsInstance<ComboBox<Account>>()
            .single { it.label == "Account" || it.label == "Origin account" }

        assertThat(accountField.listDataView.items.toList()).containsExactly(activeAccount)
    }

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

        assertThat(components.filterIsInstance<ComboBox<Category>>().single { it.label == "Category" }.value)
            .isSameAs(selectedCategory)
        assertThat(components.filterIsInstance<ComboBox<Account>>().single { it.label == "Account" }.value)
            .isSameAs(selectedAccount)
    }

    @Test
    fun dateAndAmountFieldsUseBrazilianFormatting() {
        val dialog = StatementFormDialog(emptyList(), emptyList(), null) { _, _ -> false }
        val components = descendants(dialog)
        val dateField = components.filterIsInstance<DatePicker>().single()
        val amountField = components.filterIsInstance<BigDecimalField>().single()

        assertThat(dateField.locale.language).isEqualTo("pt")
        assertThat(dateField.locale.country).isEqualTo("BR")
        assertThat(amountField.locale.language).isEqualTo("pt")
        assertThat(amountField.locale.country).isEqualTo("BR")
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
        components.filterIsInstance<ComboBox<Category>>().single { it.label == "Category" }.value = category
        components.filterIsInstance<ComboBox<Account>>().single { it.label == "Account" }.value = account
        components.filterIsInstance<BigDecimalField>().single().value = BigDecimal("18.25")

        components.filterIsInstance<Button>().single { it.text == "Save" }.click()

        assertThat(submittedId).isNull()
        assertThat(submittedData?.date).isEqualTo(LocalDate.of(2026, 10, 28))
        assertThat(submittedData?.description).isEqualTo("Lunch")
        assertThat(submittedData?.categoryId).isEqualTo(1)
        assertThat(submittedData?.accountId).isEqualTo(2)
        assertThat(submittedData?.amount).isEqualByComparingTo("18.25")
    }

    @Test
    fun saveSubmitsTransferWithoutCategoryAndWithBothAccounts() {
        val origin = Account(2, "Checking")
        val destination = Account(3, "Savings")
        var submittedData: StatementFormData? = null
        val dialog = StatementFormDialog(
            listOf(Category(1, "Food")),
            listOf(origin, destination),
            null
        ) { _, data ->
            submittedData = data
            false
        }
        val components = descendants(dialog)
        components.filterIsInstance<DatePicker>().single().value = LocalDate.of(2026, 10, 28)
        components.filterIsInstance<TextField>().single().value = "Transfer"
        components.filterIsInstance<BigDecimalField>().single().value = BigDecimal("50.00")
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

    private fun descendants(component: Component): List<Component> =
        listOf(component) + component.children.toList().flatMap(::descendants)
}
