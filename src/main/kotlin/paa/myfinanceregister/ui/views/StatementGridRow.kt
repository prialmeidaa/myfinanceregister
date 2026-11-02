package paa.myfinanceregister.ui.views

import paa.myfinanceregister.domain.model.Statement
import java.math.BigDecimal
import java.time.YearMonth

data class StatementGridRow(
    val date: String,
    val description: String,
    val category: String,
    val account: String,
    val amount: BigDecimal,
    val statement: Statement? = null
)

object StatementGridRowFactory {
    fun create(statements: List<Statement>): List<StatementGridRow> =
        statements.groupBy { YearMonth.from(it.date) }
            .toSortedMap(reverseOrder())
            .flatMap { (month, monthStatements) -> rowsForMonth(month, monthStatements) }

    private fun rowsForMonth(month: YearMonth, statements: List<Statement>): List<StatementGridRow> =
        buildList {
            statements.groupBy { it.date }.toSortedMap(reverseOrder()).forEach { (date, dayStatements) ->
                dayStatements.forEach { add(it.toGridRow()) }
                add(StatementGridRow(date.toString(), "Daily total", "", "", dayStatements.sumAmounts()))
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
        date.toString(),
        description,
        category.description,
        account.description,
        amount,
        this
    )

    private fun List<Statement>.sumAmounts(): BigDecimal =
        fold(BigDecimal.ZERO) { total, statement -> total + statement.amount }
}
