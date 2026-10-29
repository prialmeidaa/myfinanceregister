package paa.myfinanceregister.ui.components

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.BigDecimalField
import com.vaadin.flow.component.textfield.TextField
import paa.myfinanceregister.domain.model.Account
import paa.myfinanceregister.domain.model.Category
import paa.myfinanceregister.domain.model.Statement
import paa.myfinanceregister.services.StatementFormData

class StatementFormDialog(
    categories: List<Category>,
    accounts: List<Account>,
    statement: Statement?,
    private val onSave: (Long?, StatementFormData) -> Boolean
) : Dialog() {
    private val dateField = DatePicker("Date").apply {
        isRequired = true
        value = statement?.date
    }
    private val descriptionField = TextField("Description").apply {
        isRequired = true
        value = statement?.description.orEmpty()
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
        setItems(if (statement == null) accounts.filter { it.active } else accounts)
        value = accounts.firstOrNull { it.id == statement?.account?.id }
    }
    private val amountField = BigDecimalField("Amount").apply {
        isRequired = true
        value = statement?.amount
    }

    init {
        headerTitle = if (statement == null) "New statement" else "Edit statement"
        add(
            VerticalLayout(
                dateField,
                descriptionField,
                categoryField,
                accountField,
                amountField,
                HorizontalLayout(
                    Button("Cancel") { close() },
                    Button("Save") { save(statement) }
                )
            )
        )
    }

    private fun save(statement: Statement?) {
        val data = StatementFormData(
            dateField.value,
            descriptionField.value,
            categoryField.value?.id,
            accountField.value?.id,
            amountField.value
        )
        if (onSave(statement?.id, data)) close()
    }
}
