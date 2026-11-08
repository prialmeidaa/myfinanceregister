package paa.myfinanceregister.services

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import paa.myfinanceregister.domain.model.Account
import paa.myfinanceregister.domain.model.Category
import paa.myfinanceregister.domain.model.Statement
import paa.myfinanceregister.domain.model.StatementTransactionType
import paa.myfinanceregister.domain.repository.AccountRepository
import paa.myfinanceregister.domain.repository.CategoryRepository
import paa.myfinanceregister.domain.repository.StatementRepository
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Optional

class StatementSeriesUpdateTests {
    private val statements = Mockito.mock(StatementRepository::class.java)
    private val category = Category(1, "Food")
    private val account = Account(2, "Checking")
    private val service = StatementService(
        statements,
        Mockito.mock(CategoryRepository::class.java).also {
            Mockito.`when`(it.findById(1)).thenReturn(Optional.of(category))
        },
        Mockito.mock(AccountRepository::class.java).also {
            Mockito.`when`(it.findById(2)).thenReturn(Optional.of(account))
        }
    )

    @Test
    fun convertingRecurringSeriesToSmallerInstallmentCountDeletesExcessFutureStatements() {
        val current = recurringStatement(1)
        val future = (2..12).map { recurringStatement(it.toLong(), it) }
        Mockito.`when`(statements.findById(1)).thenReturn(Optional.of(current))
        Mockito.`when`(statements.findBySeriesIdAndSeriesIndexGreaterThanOrderBySeriesIndexAsc("series", 1))
            .thenReturn(future)
        Mockito.`when`(statements.save(Mockito.any(Statement::class.java)))
            .thenAnswer { it.arguments[0] }

        service.save(1, data(current.date).copy(
            transactionType = StatementTransactionType.INSTALLMENT,
            installmentNumber = 1,
            installmentCount = 3,
            updateFuture = true
        ))

        assertThat(current.transactionType).isEqualTo(StatementTransactionType.INSTALLMENT)
        assertThat(current.seriesIndex).isEqualTo(1)
        assertThat(current.seriesCount).isEqualTo(3)
        assertThat(future.take(2).map { it.seriesIndex }).containsExactly(2, 3)
        assertThat(future.take(2).map { it.seriesCount }).containsExactly(3, 3)
        assertThat(future.take(2).map { it.transactionType })
            .containsExactly(StatementTransactionType.INSTALLMENT, StatementTransactionType.INSTALLMENT)
        future.drop(2).forEach { Mockito.verify(statements).delete(it) }
    }

    private fun data(date: LocalDate) = StatementFormData(
        date, "Purchase", 1, 2, BigDecimal("25.50")
    )

    private fun recurringStatement(id: Long, index: Int = 1) = Statement(
        id = id,
        date = LocalDate.of(2026, 10, 28).plusMonths((index - 1).toLong()),
        description = "Old",
        category = category,
        account = account,
        amount = BigDecimal.ONE,
        transactionType = StatementTransactionType.RECURRING,
        seriesId = "series",
        seriesIndex = index,
        seriesCount = 12
    )
}
