package paa.myfinanceregister.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import paa.myfinanceregister.domain.model.Statement
import paa.myfinanceregister.domain.repository.AccountRepository
import paa.myfinanceregister.domain.repository.CategoryRepository
import paa.myfinanceregister.domain.repository.StatementRepository
import java.math.BigDecimal
import java.time.LocalDate

data class StatementFormData(
    val date: LocalDate?,
    val description: String,
    val categoryId: Long?,
    val accountId: Long?,
    val amount: BigDecimal?
)

@Service
class StatementService(
    private val repository: StatementRepository,
    private val categoryRepository: CategoryRepository,
    private val accountRepository: AccountRepository
) {
    @Transactional(readOnly = true)
    fun findAll(): List<Statement> = repository.findAllByOrderByDateDesc()

    @Transactional
    fun save(id: Long?, data: StatementFormData): Statement {
        val description = data.description.trim()
        require(description.isNotEmpty()) { "Description is required" }
        val date = requireNotNull(data.date) { "Date is required" }
        val amount = requireNotNull(data.amount) { "Amount is required" }
        val category = categoryRepository.findById(requireNotNull(data.categoryId) {
            "Category is required"
        }).orElseThrow { NoSuchElementException("Category ${data.categoryId} not found") }
        val account = accountRepository.findById(requireNotNull(data.accountId) {
            "Account is required"
        }).orElseThrow { NoSuchElementException("Account ${data.accountId} not found") }
        val statement = id?.let(::loadStatement) ?: Statement()

        statement.date = date
        statement.description = description
        statement.category = category
        statement.account = account
        statement.amount = amount
        return repository.save(statement)
    }

    @Transactional
    fun delete(id: Long) {
        repository.delete(loadStatement(id))
    }

    private fun loadStatement(id: Long): Statement =
        repository.findById(id).orElseThrow { NoSuchElementException("Statement $id not found") }
}
