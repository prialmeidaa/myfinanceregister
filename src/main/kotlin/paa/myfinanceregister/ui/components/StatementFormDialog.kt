package paa.myfinanceregister.ui.components

import com.vaadin.flow.component.button.Button
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
import java.util.Locale

class StatementFormDialog(
    categories: List<Category>,
    accounts: List<Account>,
    statement: Statement?,
    private val onSave: (Long?, StatementFormData) -> Boolean
) : Dialog() {
    private val dateField = DatePicker("Date").apply {
        locale = BRAZILIAN_LOCALE
        setI18n(DatePicker.DatePickerI18n().setDateFormat(DATE_FORMAT))
        isRequired = true
        value = statement?.date
    }
    private val descriptionField = TextField("Description").apply {
        isRequired = true
        setMaxLength(Statement.MAX_DESCRIPTION_LENGTH)
        setMaxWidth(DESCRIPTION_FIELD_MAX_WIDTH)
        value = statement?.description.orEmpty()
    }
    private val financialFields = StatementFinancialFields(
        categories,
        if (statement == null) accounts.filter { it.active } else accounts,
        statement,
        includeSelectionFields = true
    )
    private val amountField = BigDecimalField("Amount").apply {
        locale = BRAZILIAN_LOCALE
        isRequired = true
        value = statement?.amount
    }
    private val scheduleFields = StatementScheduleFields(statement)

    init {
        headerTitle = if (statement == null) "New statement" else "Edit statement"
        add(
            VerticalLayout(
                dateField,
                descriptionField,
                financialFields,
                amountField,
                scheduleFields,
                HorizontalLayout(
                    Button("Cancel") { close() },
                    Button("Save") { save(statement) }
                )
            )
        )
    }

    private fun save(statement: Statement?) {
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
        StatementSaveConfirmation.submit(statement, data, { onSave(statement?.id, it) }) { close() }
    }

    private companion object {
        const val DATE_FORMAT = "dd/MM/yyyy"
        const val DESCRIPTION_FIELD_MAX_WIDTH = "24rem"
        val BRAZILIAN_LOCALE: Locale = Locale.forLanguageTag("pt-BR")
    }
}
