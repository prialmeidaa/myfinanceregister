package paa.myfinanceregister.services

import paa.myfinanceregister.domain.model.StatementTransactionType
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.temporal.ChronoUnit

internal data class ScheduledStatement(val date: LocalDate, val index: Int, val count: Int)

internal object StatementSchedule {
    fun create(data: StatementFormData, date: LocalDate, currentYear: Year): List<ScheduledStatement> =
        when (data.transactionType) {
            StatementTransactionType.SINGLE -> listOf(ScheduledStatement(date, 1, 1))
            StatementTransactionType.RECURRING -> recurringDates(date, currentYear)
            StatementTransactionType.INSTALLMENT -> installmentDates(
                date,
                requireNotNull(data.installmentNumber) { "Installment number is required" },
                requireNotNull(data.installmentCount) { "Installment count is required" }
            )
        }

    private fun recurringDates(date: LocalDate, year: Year): List<ScheduledStatement> {
        val lastMonth = YearMonth.of(year.value, 12)
        if (YearMonth.from(date) > lastMonth) return listOf(ScheduledStatement(date, 1, 1))
        val count = ChronoUnit.MONTHS.between(YearMonth.from(date), lastMonth).toInt() + 1
        return (0 until count).map { offset ->
            ScheduledStatement(date.plusMonths(offset.toLong()), offset + 1, count)
        }
    }

    private fun installmentDates(
        date: LocalDate,
        installmentNumber: Int,
        installmentCount: Int
    ): List<ScheduledStatement> {
        require(installmentNumber > 0) { "Installment number must be positive" }
        require(installmentCount >= installmentNumber) { "Installment count must be at least the installment number" }
        return (installmentNumber..installmentCount).map { number ->
            ScheduledStatement(date.plusMonths((number - installmentNumber).toLong()), number, installmentCount)
        }
    }
}
