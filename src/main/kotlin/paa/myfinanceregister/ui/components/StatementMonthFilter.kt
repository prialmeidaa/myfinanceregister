package paa.myfinanceregister.ui.components

import com.vaadin.flow.component.combobox.MultiSelectComboBox
import paa.myfinanceregister.domain.model.Statement
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

class StatementMonthFilter(
    private val onSelectionChange: () -> Unit
) : MultiSelectComboBox<YearMonth>("Months") {
    private var updatingOptions = false

    init {
        placeholder = "All months"
        setItemLabelGenerator { it.format(MONTH_FORMATTER) }
        setClearButtonVisible(true)
        addValueChangeListener { if (!updatingOptions) onSelectionChange() }
    }

    fun updateMonths(statements: List<Statement>) {
        val selectedMonths = value
        val months = statements.map { YearMonth.from(it.date) }.distinct().sortedDescending()
        updatingOptions = true
        try {
            setItems(months)
            value = selectedMonths.intersect(months.toSet())
        } finally {
            updatingOptions = false
        }
    }

    fun filter(statements: List<Statement>): List<Statement> {
        val selectedMonths = value
        if (selectedMonths.isEmpty()) return statements
        return statements.filter { YearMonth.from(it.date) in selectedMonths }
    }

    private companion object {
        val MONTH_FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)
    }
}
