package paa.myfinanceregister.ui.components

import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import paa.myfinanceregister.domain.model.Account
import paa.myfinanceregister.domain.model.Category
import paa.myfinanceregister.domain.model.Statement
import paa.myfinanceregister.domain.model.StatementKind

internal data class StatementFinancialSelection(
    val kind: StatementKind,
    val categoryId: Long?,
    val accountId: Long?,
    val destinationAccountId: Long?
)

internal class StatementFinancialFields(
    categories: List<Category>,
    accounts: List<Account>,
    statement: Statement?
) : VerticalLayout() {
    private val kindField = RadioButtonGroup<StatementKind>("Transaction type").apply {
        setItems(StatementKind.entries)
        setItemLabelGenerator {
            when (it) {
                StatementKind.INCOME -> "Income"
                StatementKind.EXPENSE -> "Expense"
                StatementKind.TRANSFER -> "Transfer"
            }
        }
        value = statement?.kind ?: StatementKind.EXPENSE
    }
    private val categoryField = ComboBox<Category>("Category").apply {
        isRequired = true
        setItemLabelGenerator { it.description }
        setItems(categories)
        value = categories.firstOrNull { it.id == statement?.category?.id }
    }
    private val accountField = ComboBox<Account>("Account").apply {
        isRequired = true
        setItemLabelGenerator { it.description }
        setItems(accounts)
        value = accounts.firstOrNull { it.id == statement?.account?.id }
    }
    private val destinationAccountField = ComboBox<Account>("Destination account").apply {
        isRequired = true
        setItemLabelGenerator { it.description }
        setItems(accounts)
        value = accounts.firstOrNull { it.id == statement?.destinationAccount?.id }
    }
    private val accountFields = HorizontalLayout(categoryField, accountField, destinationAccountField).apply {
        setWidthFull()
        setFlexGrow(1.0, categoryField, accountField, destinationAccountField)
    }

    init {
        add(kindField, accountFields)
        setWidthFull()
        setPadding(false)
        kindField.addValueChangeListener { updateVisibility() }
        updateVisibility()
    }

    fun selection() = StatementFinancialSelection(
        kindField.value,
        categoryField.value?.id.takeUnless { kindField.value == StatementKind.TRANSFER },
        accountField.value?.id,
        destinationAccountField.value?.id.takeIf { kindField.value == StatementKind.TRANSFER }
    )

    private fun updateVisibility() {
        val isTransfer = kindField.value == StatementKind.TRANSFER
        categoryField.isVisible = !isTransfer
        categoryField.isRequired = !isTransfer
        accountField.label = if (isTransfer) "Origin account" else "Account"
        destinationAccountField.isVisible = isTransfer
    }
}
