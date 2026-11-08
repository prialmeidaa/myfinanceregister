package paa.myfinanceregister.services

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import paa.myfinanceregister.domain.model.StatementTransactionType
import java.time.LocalDate
import java.time.Year

class StatementScheduleTests {
    @Test
    fun createsInstallmentsMonthlyStartingAtTheEnteredInstallment() {
        val baseDate = LocalDate.of(2026, 10, 28)
        val schedule = StatementSchedule.create(
            installmentData(2, 3), baseDate, Year.of(2026)
        )

        assertThat(schedule.map { it.date }).containsExactly(
            baseDate, baseDate.plusMonths(1)
        )
        assertThat(schedule.map { it.index }).containsExactly(2, 3)
    }

    @Test
    fun rejectsAnInstallmentNumberGreaterThanTheTotal() {
        assertThatThrownBy {
            StatementSchedule.create(installmentData(4, 3), LocalDate.now(), Year.of(2026))
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    private fun installmentData(number: Int, count: Int) = StatementFormData(
        LocalDate.of(2026, 10, 28), "Purchase", 1, 2, null,
        StatementTransactionType.INSTALLMENT, number, count
    )
}
