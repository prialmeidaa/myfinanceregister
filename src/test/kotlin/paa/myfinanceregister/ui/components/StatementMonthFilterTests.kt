package paa.myfinanceregister.ui.components

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import paa.myfinanceregister.domain.model.Account
import paa.myfinanceregister.domain.model.Category
import paa.myfinanceregister.domain.model.Statement
import java.time.LocalDate
import java.time.YearMonth

class StatementMonthFilterTests {
    @Test
    fun defaultsToTheCurrentMonthEvenWhenThereAreNoStatementsInThatMonth() {
        val statements = listOf(statement(YearMonth.now().minusMonths(1).atDay(5)))
        val filter = StatementMonthFilter {}
        filter.updateMonths(statements)

        assertThat(filter.value).containsExactly(YearMonth.now())
        assertThat(filter.filter(statements)).isEmpty()
    }

    @Test
    fun filtersByAllSelectedMonths() {
        val statements = listOf(
            statement(LocalDate.of(2026, 10, 5)),
            statement(LocalDate.of(2026, 9, 5)),
            statement(LocalDate.of(2026, 8, 5))
        )
        val filter = StatementMonthFilter {}
        filter.updateMonths(statements)
        filter.value = setOf(YearMonth.of(2026, 10), YearMonth.of(2026, 8))

        assertThat(filter.filter(statements)).containsExactly(statements[0], statements[2])
    }

    @Test
    fun emptyMonthSelectionLeavesEveryStatementVisible() {
        val statements = listOf(statement(LocalDate.of(2026, 10, 5)))
        val filter = StatementMonthFilter {}
        filter.value = emptySet()

        assertThat(filter.filter(statements)).containsExactlyElementsOf(statements)
    }

    private fun statement(date: LocalDate) = Statement(
        date = date,
        description = "Purchase",
        category = Category(1, "Food"),
        account = Account(1, "Checking")
    )
}
