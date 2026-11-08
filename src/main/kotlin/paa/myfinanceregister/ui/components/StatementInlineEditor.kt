package paa.myfinanceregister.ui.components

import com.vaadin.flow.component.button.Button
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
    private val financialFields = StatementFinancialFields(categories, accounts, statement)
    private val amountField = BigDecimalField("Amount").apply {
        locale = BRAZILIAN_LOCALE
        isRequired = true
        value = statement.amount
    }
    private val scheduleFields = StatementScheduleFields(statement)

    init {
        add(
            HorizontalLayout(dateField, descriptionField, financialFields, amountField),
            scheduleFields,
            HorizontalLayout(
                Button("Cancel", { onCancel() }),
                Button("Save", { save() })
            )
        )
        setWidthFull()
    }

    private fun save() {
        val schedule = scheduleFields.selection()
        val financial = financialFields.selection()
        val data = StatementFormData(
            dateField.value,
            descriptionField.value,
            financial.categoryId,
            financial.accountId,
            amountField.value,
            schedule.type,
            schedule.installmentNumber,
            schedule.installmentCount,
            kind = financial.kind,
            destinationAccountId = financial.destinationAccountId
        )
        StatementSaveConfirmation.submit(statement, data, { onSave(statement.id, it) }) { onCancel() }
    }

    private companion object {
        const val DATE_FORMAT = "dd/MM/yyyy"
        val BRAZILIAN_LOCALE: Locale = Locale.forLanguageTag("pt-BR")
    }
}
