package paa.myfinanceregister.ui.views

import paa.myfinanceregister.domain.model.Statement
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.LocalDate
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
    val statement: Statement? = null,
    val totalType: StatementTotalType? = null
)

enum class StatementTotalType {
    DAILY,
    MONTHLY
}

object StatementGridRowFactory {
    fun formatAmount(amount: BigDecimal): String =
        NumberFormat.getNumberInstance(BRAZILIAN_LOCALE).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }.format(amount)

    fun create(statements: List<Statement>): List<StatementGridRow> {
        val runningTotals = runningTotalsByDate(statements)
        return statements.groupBy { YearMonth.from(it.date) }
            .toSortedMap(reverseOrder())
            .values
            .flatMap { monthStatements -> rowsForMonth(monthStatements, runningTotals) }
    }

    private fun rowsForMonth(
        statements: List<Statement>,
        runningTotals: Map<LocalDate, BigDecimal>
    ): List<StatementGridRow> =
        buildList {
            val statementsByDate = statements.groupBy { it.date }
            statementsByDate.toSortedMap(reverseOrder()).forEach { (date, dayStatements) ->
                dayStatements.forEach { add(it.toGridRow()) }
                add(
                    StatementGridRow(
                        "", "", "", "", runningTotals.getValue(date), totalType = StatementTotalType.DAILY
                    )
                )
            }
            add(
                StatementGridRow(
                    "", "", "", "", statements.sumAmounts(), totalType = StatementTotalType.MONTHLY
                )
            )
        }

    private fun runningTotalsByDate(statements: List<Statement>): Map<LocalDate, BigDecimal> {
        var runningTotal = BigDecimal.ZERO
        return statements.groupBy { it.date }.toSortedMap().mapValues { (_, dayStatements) ->
            runningTotal += dayStatements.sumAmounts()
            runningTotal
        }
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
