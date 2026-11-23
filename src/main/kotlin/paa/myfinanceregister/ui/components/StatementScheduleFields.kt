package paa.myfinanceregister.ui.components

import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import com.vaadin.flow.component.textfield.IntegerField
import paa.myfinanceregister.domain.model.Statement
import paa.myfinanceregister.domain.model.StatementTransactionType

internal data class StatementScheduleSelection(
    val type: StatementTransactionType,
    val installmentNumber: Int?,
    val installmentCount: Int?
)

internal class StatementScheduleFields(statement: Statement?) : VerticalLayout() {
    private val typeField = RadioButtonGroup<StatementTransactionType>("Schedule").apply {
        addClassName("statement-inline-editor-horizontal-options")
        setItems(StatementTransactionType.entries)
        setItemLabelGenerator {
            when (it) {
                StatementTransactionType.SINGLE -> "Single transaction"
                StatementTransactionType.RECURRING -> "Recurring"
                StatementTransactionType.INSTALLMENT -> "Installment"
            }
        }
        value = statement?.transactionType ?: StatementTransactionType.SINGLE
    }
    private val installmentNumberField = IntegerField("Installment number").apply {
        min = 1
        max = 99
        value = statement?.seriesIndex ?: 1
        width = "10rem"
        setMaxWidth("10rem")
        addClassName("statement-inline-editor-installment-field")
    }
    private val installmentCountField = IntegerField("Total installments").apply {
        min = 1
        max = 99
        value = statement?.seriesCount ?: 2
        width = "10rem"
        setMaxWidth("10rem")
        addClassName("statement-inline-editor-installment-field")
    }
    private val recurringNote =
        Paragraph("Recurring transactions will continue through the end of the current year.")
    private val installmentFields = HorizontalLayout(installmentNumberField, installmentCountField).apply {
        setWidth("21rem")
        setWrap(false)
        setSpacing(false)
        addClassName("statement-inline-editor-installment-fields")
        setFlexGrow(0.0, installmentNumberField, installmentCountField)
    }

    init {
        add(typeField, installmentFields, recurringNote)
        setWidthFull()
        setPadding(false)
        addClassName("statement-inline-editor-schedule-fields")
        typeField.addValueChangeListener { updateVisibility() }
        updateVisibility()
    }

    fun selection(): StatementScheduleSelection {
        val isInstallment = typeField.value == StatementTransactionType.INSTALLMENT
        return StatementScheduleSelection(
            typeField.value,
            installmentNumberField.value.takeIf { isInstallment },
            installmentCountField.value.takeIf { isInstallment }
        )
    }

    private fun updateVisibility() {
        val installment = typeField.value == StatementTransactionType.INSTALLMENT
        installmentNumberField.isVisible = installment
        installmentCountField.isVisible = installment
        recurringNote.isVisible = typeField.value == StatementTransactionType.RECURRING
    }
}
