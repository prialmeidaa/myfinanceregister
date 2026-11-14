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
        value = statement?.seriesIndex ?: 1
    }
    private val installmentCountField = IntegerField("Total installments").apply {
        min = 1
        value = statement?.seriesCount ?: 2
    }
    private val recurringNote =
        Paragraph("Recurring transactions will continue through the end of the current year.")
    private val installmentFields = HorizontalLayout(installmentNumberField, installmentCountField).apply {
        setWidthFull()
        setFlexGrow(1.0, installmentNumberField, installmentCountField)
    }

    init {
        add(typeField, installmentFields, recurringNote)
        setWidthFull()
        setPadding(false)
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
