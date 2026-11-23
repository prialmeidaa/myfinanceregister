package paa.myfinanceregister.ui.components

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import com.vaadin.flow.component.textfield.IntegerField
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import paa.myfinanceregister.domain.model.StatementTransactionType
import paa.myfinanceregister.services.StatementFormData

class StatementScheduleFieldsTests {
    @Test
    fun installmentSelectionShowsAndSubmitsInstallmentValues() {
        var submitted: StatementFormData? = null
        val dialog = StatementFormDialog(emptyList(), emptyList(), null) { _, data ->
            submitted = data
            false
        }
        val components = descendants(dialog)
        components.filterIsInstance<RadioButtonGroup<StatementTransactionType>>()
            .single { it.label == "Schedule" }.value =
            StatementTransactionType.INSTALLMENT
        components.filterIsInstance<IntegerField>().first { it.label == "Installment number" }.value = 1
        components.filterIsInstance<IntegerField>().first { it.label == "Total installments" }.value = 3

        assertThat(components.filterIsInstance<IntegerField>().all { it.isVisible }).isTrue()
        components.filterIsInstance<IntegerField>().forEach {
            assertThat(it.width).isEqualTo("10rem")
            assertThat(it.max).isEqualTo(99)
            assertThat(it.classNames).contains("statement-inline-editor-installment-field")
        }
        components.filterIsInstance<Button>().single { it.text == "Save" }.click()
        assertThat(submitted?.transactionType).isEqualTo(StatementTransactionType.INSTALLMENT)
        assertThat(submitted?.installmentNumber).isEqualTo(1)
        assertThat(submitted?.installmentCount).isEqualTo(3)
    }

    @Test
    fun recurringSelectionShowsEndOfYearInformation() {
        val fields = StatementScheduleFields(null)
        val typeField = descendants(fields).filterIsInstance<RadioButtonGroup<StatementTransactionType>>().single()
        val note = descendants(fields).filterIsInstance<Paragraph>().single()

        assertThat(typeField.value).isEqualTo(StatementTransactionType.SINGLE)
        assertThat(typeField.classNames).contains("statement-inline-editor-horizontal-options")
        assertThat(note.isVisible).isFalse()
        typeField.value = StatementTransactionType.RECURRING
        assertThat(note.text).contains("end of the current year")
        assertThat(note.isVisible).isTrue()
    }

    private fun descendants(component: Component): List<Component> =
        listOf(component) + component.children.toList().flatMap(::descendants)
}
