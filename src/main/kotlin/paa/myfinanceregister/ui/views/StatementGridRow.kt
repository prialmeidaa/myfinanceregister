package paa.myfinanceregister.ui.views

import paa.myfinanceregister.domain.model.Statement
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val BRAZILIAN_LOCALE = Locale.forLanguageTag("pt-BR")

data class StatementGridRow(
    val date: String,
    val description: String,
    val category: String,
    val account: String,
    val amount: BigDecimal,
    val statement: Statement? = null
)

object StatementGridRowFactory {
    fun formatAmount(amount: BigDecimal): String =
        NumberFormat.getNumberInstance(BRAZILIAN_LOCALE).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }.format(amount)

    fun create(statements: List<Statement>): List<StatementGridRow> =
        statements.groupBy { YearMonth.from(it.date) }
            .toSortedMap(reverseOrder())
            .flatMap { (month, monthStatements) -> rowsForMonth(month, monthStatements) }

    private fun rowsForMonth(month: YearMonth, statements: List<Statement>): List<StatementGridRow> =
        buildList {
            statements.groupBy { it.date }.toSortedMap(reverseOrder()).forEach { (date, dayStatements) ->
                dayStatements.forEach { add(it.toGridRow()) }
                add(StatementGridRow(date.format(DATE_FORMATTER), "Daily total", "", "", dayStatements.sumAmounts()))
            }
            add(
                StatementGridRow(
                    month.toString(),
                    "Monthly total",
                    "",
                    "",
                    statements.sumAmounts()
                )
            )
        }

    private fun Statement.toGridRow() = StatementGridRow(
        date.format(DATE_FORMATTER),
        description,
        category.description,
        account.description,
        amount,
        this
    )

    private fun List<Statement>.sumAmounts(): BigDecimal =
        fold(BigDecimal.ZERO) { total, statement -> total + statement.amount }
}
