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
import java.time.Year
import java.util.Optional

class StatementSeriesServiceTests {
    private val statements = Mockito.mock(StatementRepository::class.java)
    private val categories = Mockito.mock(CategoryRepository::class.java)
    private val accounts = Mockito.mock(AccountRepository::class.java)
    private val service = StatementService(statements, categories, accounts)
    private val category = Category(1, "Food")
    private val account = Account(2, "Checking")

    @Test
    fun createsOneStatementForEachInstallmentFromTheSelectedNumber() {
        prepareReferences()
        val saved = mutableListOf<Statement>()
        Mockito.`when`(statements.save(Mockito.any(Statement::class.java)))
            .thenAnswer { (it.arguments[0] as Statement).also { item -> saved.add(item) } }
        val baseDate = LocalDate.of(2026, 10, 28)

        service.save(null, data(baseDate).copy(
            transactionType = StatementTransactionType.INSTALLMENT,
            installmentNumber = 1,
            installmentCount = 3
        ))

        assertThat(saved.map { it.date }).containsExactly(
            baseDate, baseDate.plusMonths(1), baseDate.plusMonths(2)
        )
        assertThat(saved.map { it.seriesIndex }).containsExactly(1, 2, 3)
        assertThat(saved.map { it.seriesId }.distinct()).hasSize(1).doesNotContainNull()
    }

    @Test
    fun createsRecurringStatementsThroughDecemberOfTheCurrentYear() {
        prepareReferences()
        val saved = mutableListOf<Statement>()
        Mockito.`when`(statements.save(Mockito.any(Statement::class.java)))
            .thenAnswer { (it.arguments[0] as Statement).also { item -> saved.add(item) } }
        val baseDate = LocalDate.of(Year.now().value, 1, 15)

        service.save(null, data(baseDate).copy(transactionType = StatementTransactionType.RECURRING))

        assertThat(saved).hasSize(12)
        assertThat(saved.last().date).isEqualTo(LocalDate.of(Year.now().value, 12, 15))
    }

    private fun data(date: LocalDate) = StatementFormData(
        date, "Purchase", 1, 2, BigDecimal("25.50")
    )

    private fun prepareReferences() {
        Mockito.`when`(categories.findById(1)).thenReturn(Optional.of(category))
        Mockito.`when`(accounts.findById(2)).thenReturn(Optional.of(account))
    }

}
