package paa.myfinanceregister.ui.components

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.details.DetailsVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.BigDecimalField
import com.vaadin.flow.component.textfield.TextField
import paa.myfinanceregister.domain.model.Account
import paa.myfinanceregister.domain.model.Category
import paa.myfinanceregister.domain.model.Statement
import paa.myfinanceregister.domain.model.StatementKind
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
        setMaxLength(Statement.MAX_DESCRIPTION_LENGTH)
        setMaxWidth(DESCRIPTION_FIELD_MAX_WIDTH)
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
    private val kindHeader = HorizontalLayout(financialFields.kindField).apply {
        setWidthFull()
        addClassName("statement-inline-editor-kind-header")
    }
    private val primaryFields = HorizontalLayout(
        dateField,
        descriptionField,
        financialFields.categoryField,
        financialFields.accountField,
        amountField
    ).apply {
        setWidthFull()
        setWrap(true)
        setFlexGrow(1.0, descriptionField)
        addClassName("statement-inline-editor-row")
        addClassName("statement-inline-editor-primary-row")
    }
    private val financialFieldsRow = HorizontalLayout(financialFields.destinationAccountField).apply {
        setWidthFull()
        addClassName("statement-inline-editor-row")
        addClassName("statement-inline-editor-financial-row")
    }
    private val scheduleOptions = Details("More options", scheduleFields).apply {
        setWidthFull()
        isOpened = false
        addThemeVariants(DetailsVariant.SMALL, DetailsVariant.FILLED)
        addClassName("statement-inline-editor-schedule-options")
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
            kindHeader,
            primaryFields,
            financialFieldsRow,
            scheduleOptions,
            actions
        )
        setWidthFull()
        setPadding(false)
        addClassName("statement-inline-editor")
        updateFinancialVisibility()
        financialFields.kindField.addValueChangeListener { updateFinancialVisibility() }
    }

    private fun updateFinancialVisibility() {
        financialFieldsRow.isVisible =
            financialFields.kindField.value == StatementKind.TRANSFER
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
        const val DESCRIPTION_FIELD_MAX_WIDTH = "24rem"
        val BRAZILIAN_LOCALE: Locale = Locale.forLanguageTag("pt-BR")
    }
}
