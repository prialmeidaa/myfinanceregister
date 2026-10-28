package paa.myfinanceregister.services

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import paa.myfinanceregister.domain.model.Account
import paa.myfinanceregister.domain.model.Category
import paa.myfinanceregister.domain.model.Statement
import paa.myfinanceregister.domain.repository.AccountRepository
import paa.myfinanceregister.domain.repository.CategoryRepository
import paa.myfinanceregister.domain.repository.StatementRepository
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Optional

class StatementServiceTests {
    private val statementRepository = Mockito.mock(StatementRepository::class.java)
    private val categoryRepository = Mockito.mock(CategoryRepository::class.java)
    private val accountRepository = Mockito.mock(AccountRepository::class.java)
    private val service = StatementService(statementRepository, categoryRepository, accountRepository)
    private val category = Category(1, "Food")
    private val account = Account(2, "Checking")
    private val data = StatementFormData(
        LocalDate.of(2026, 10, 28),
        "  Lunch  ",
        1,
        2,
        BigDecimal("18.25")
    )

    @Test
    fun findAllReturnsStatementsInRepositoryOrder() {
        val statements = listOf(Statement(description = "Lunch"))
        Mockito.`when`(statementRepository.findAllByOrderByDateDesc()).thenReturn(statements)

        assertThat(service.findAll()).containsExactlyElementsOf(statements)
    }

    @Test
    fun saveTrimsDescriptionAndPersistsAllStatementValuesAndRelations() {
        prepareReferences()
        Mockito.`when`(statementRepository.save(Mockito.any(Statement::class.java)))
            .thenAnswer { it.arguments[0] }

        val saved = service.save(null, data)

        assertThat(saved.date).isEqualTo(data.date)
        assertThat(saved.description).isEqualTo("Lunch")
        assertThat(saved.category).isSameAs(category)
        assertThat(saved.account).isSameAs(account)
        assertThat(saved.amount).isEqualByComparingTo("18.25")
        Mockito.verify(statementRepository).save(saved)
    }

    @Test
    fun saveUpdatesTheExistingStatement() {
        prepareReferences()
        val statement = Statement(5, description = "Old")
        Mockito.`when`(statementRepository.findById(5)).thenReturn(Optional.of(statement))
        Mockito.`when`(statementRepository.save(statement)).thenReturn(statement)

        val saved = service.save(5, data)

        assertThat(saved).isSameAs(statement)
        assertThat(saved.description).isEqualTo("Lunch")
    }

    @Test
    fun saveRejectsBlankDescriptionWithoutPersisting() {
        assertThatThrownBy { service.save(null, data.copy(description = "  ")) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Description is required")

        Mockito.verify(statementRepository, Mockito.never()).save(Mockito.any(Statement::class.java))
    }

    @Test
    fun saveRejectsMissingRequiredDate() {
        assertThatThrownBy { service.save(null, data.copy(date = null)) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Date is required")
    }

    @Test
    fun deleteRemovesAnExistingStatement() {
        val statement = Statement(5, description = "Lunch")
        Mockito.`when`(statementRepository.findById(5)).thenReturn(Optional.of(statement))

        service.delete(5)

        Mockito.verify(statementRepository).delete(statement)
    }

    @Test
    fun deleteRejectsAnUnknownStatement() {
        Mockito.`when`(statementRepository.findById(5)).thenReturn(Optional.empty())

        assertThatThrownBy { service.delete(5) }
            .isInstanceOf(NoSuchElementException::class.java)
            .hasMessage("Statement 5 not found")

        Mockito.verify(statementRepository, Mockito.never())
            .delete(Mockito.any(Statement::class.java))
    }

    private fun prepareReferences() {
        Mockito.`when`(categoryRepository.findById(1)).thenReturn(Optional.of(category))
        Mockito.`when`(accountRepository.findById(2)).thenReturn(Optional.of(account))
    }
}
