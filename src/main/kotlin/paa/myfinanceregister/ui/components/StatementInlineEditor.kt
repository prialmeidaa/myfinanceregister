package paa.myfinanceregister.ui.components

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
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
        width = "12rem"
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
        width = "12rem"
    }
    private val scheduleFields = StatementScheduleFields(statement)
    private val primaryFields = HorizontalLayout(dateField, descriptionField, amountField).apply {
        setWidthFull()
        setWrap(true)
        setFlexGrow(1.0, descriptionField)
        addClassName("statement-inline-editor-row")
        addClassName("statement-inline-editor-primary-row")
    }
    private val optionFields = HorizontalLayout(financialFields, scheduleFields).apply {
        setWidthFull()
        setWrap(true)
        setFlexGrow(1.0, financialFields, scheduleFields)
        addClassName("statement-inline-editor-row")
        addClassName("statement-inline-editor-options-row")
    }
    private val actions = HorizontalLayout(
        Button("Cancel", { onCancel() }).apply {
            addThemeVariants(ButtonVariant.TERTIARY)
        },
        Button("Save", { save() }).apply {
            addThemeVariants(ButtonVariant.PRIMARY)
        }
    ).apply {
        setWidthFull()
        addClassName("statement-inline-editor-actions")
    }

    init {
        add(
            primaryFields,
            optionFields,
            actions
        )
        setWidthFull()
        setPadding(false)
        addClassName("statement-inline-editor")
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
