package paa.myfinanceregister.ui.views

import paa.myfinanceregister.domain.model.Statement
import paa.myfinanceregister.domain.model.StatementKind
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
    DAILY
}

object StatementGridRowFactory {
    fun formatAmount(amount: BigDecimal): String =
        (if (amount.signum() > 0) "+" else "") + NumberFormat.getNumberInstance(BRAZILIAN_LOCALE).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }.format(amount)

    fun create(statements: List<Statement>): List<StatementGridRow> {
        val entries = statements.flatMap { it.toLedgerEntries() }
        val runningTotals = runningTotalsByDate(entries)
        return entries.groupBy { YearMonth.from(it.date) }
            .toSortedMap()
            .values.flatMap { monthEntries -> rowsForMonth(monthEntries, runningTotals) }
    }

    private fun rowsForMonth(
        entries: List<LedgerEntry>,
        runningTotals: Map<LocalDate, BigDecimal>
    ): List<StatementGridRow> =
        buildList {
            val entriesByDate = entries.groupBy { it.date }
            entriesByDate.toSortedMap().forEach { (date, dayEntries) ->
                dayEntries.forEach { add(it.row) }
                add(
                    StatementGridRow(
                        "", "", "", "Daily balance", runningTotals.getValue(date),
                        totalType = StatementTotalType.DAILY
                    )
                )
            }
        }

    private fun runningTotalsByDate(entries: List<LedgerEntry>): Map<LocalDate, BigDecimal> {
        var runningTotal = BigDecimal.ZERO
        return entries.groupBy { it.date }.toSortedMap().mapValues { (_, dayEntries) ->
            runningTotal += dayEntries.sumAmounts()
            runningTotal
        }
    }

    private fun Statement.toLedgerEntries(): List<LedgerEntry> {
        val transferAmount = amount.abs()
        val categoryDescription = if (kind == StatementKind.TRANSFER) "Transfer" else category?.description.orEmpty()
        val originEntry = LedgerEntry(
            date,
            StatementGridRow(
                date.format(DATE_FORMATTER),
                description,
                categoryDescription,
                account.description,
                if (kind == StatementKind.TRANSFER) transferAmount.negate() else amount,
                this
            )
        )
        if (kind != StatementKind.TRANSFER) return listOf(originEntry)
        val destination = requireNotNull(destinationAccount) { "Transfer destination account is required" }
        return listOf(
            originEntry,
            LedgerEntry(
                date,
                StatementGridRow(
                    date.format(DATE_FORMATTER),
                    description,
                    categoryDescription,
                    destination.description,
                    transferAmount
                )
            )
        )
    }

    private fun List<LedgerEntry>.sumAmounts(): BigDecimal =
        fold(BigDecimal.ZERO) { total, entry -> total + entry.row.amount }

    private data class LedgerEntry(val date: LocalDate, val row: StatementGridRow)
}
