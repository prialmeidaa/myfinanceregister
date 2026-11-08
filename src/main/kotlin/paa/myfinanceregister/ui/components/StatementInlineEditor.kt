package paa.myfinanceregister.ui.components

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.BigDecimalField
import com.vaadin.flow.component.textfield.TextField
import paa.myfinanceregister.domain.model.Account
import paa.myfinanceregister.domain.model.Category
import paa.myfinanceregister.domain.model.Statement
import paa.myfinanceregister.services.StatementFormData
import java.util.Locale

class StatementInlineEditor(
    categories: List<Category>,
    accounts: List<Account>,
    private val statement: Statement,
    private val onSave: (Long?, StatementFormData) -> Boolean,
    private val onCancel: () -> Unit
) : VerticalLayout() {
    private val dateField = DatePicker("Date").apply {
        locale = BRAZILIAN_LOCALE
        setI18n(DatePicker.DatePickerI18n().setDateFormat(DATE_FORMAT))
        isRequired = true
        value = statement.date
    }
    private val descriptionField = TextField("Description").apply {
        isRequired = true
        value = statement.description
    }
    private val categoryField = ComboBox<Category>("Category").apply {
        isRequired = true
        setItemLabelGenerator { it.description }
        setItems(categories)
        value = categories.firstOrNull { it.id == statement.category.id }
    }
    private val accountField = ComboBox<Account>("Account").apply {
        isRequired = true
        setItemLabelGenerator { it.description }
        setItems(accounts)
        value = accounts.firstOrNull { it.id == statement.account.id }
    }
    private val amountField = BigDecimalField("Amount").apply {
        locale = BRAZILIAN_LOCALE
        isRequired = true
        value = statement.amount
    }

    init {
        add(
            HorizontalLayout(dateField, descriptionField, categoryField, accountField, amountField),
            HorizontalLayout(
                Button("Cancel", { onCancel() }),
                Button("Save", { save() })
            )
        )
        setWidthFull()
    }

    private fun save() {
        val data = StatementFormData(
            dateField.value,
            descriptionField.value,
            categoryField.value?.id,
            accountField.value?.id,
            amountField.value
        )
        if (onSave(statement.id, data)) onCancel()
    }

    private companion object {
        const val DATE_FORMAT = "dd/MM/yyyy"
        val BRAZILIAN_LOCALE: Locale = Locale.forLanguageTag("pt-BR")
    }
}
