package paa.myfinanceregister.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import paa.myfinanceregister.domain.model.Statement
import paa.myfinanceregister.domain.repository.AccountRepository
import paa.myfinanceregister.domain.repository.CategoryRepository
import paa.myfinanceregister.domain.repository.StatementRepository
import java.math.BigDecimal
import java.time.LocalDate

@Service
class StatementService(
    private val repository: StatementRepository,
    private val categoryRepository: CategoryRepository,
    private val accountRepository: AccountRepository
) {
    private val seriesManager = StatementSeriesManager(repository)

    @Transactional(readOnly = true)
    fun findAll(): List<Statement> = repository.findAllByOrderByDateDesc()

    @Transactional
    fun save(id: Long?, data: StatementFormData): Statement {
        val validated = validate(data)
        return seriesManager.save(id?.let(::loadStatement), data, validated)
    }

    @Transactional
    fun delete(id: Long) {
        repository.delete(loadStatement(id))
    }

    private fun loadStatement(id: Long): Statement =
        repository.findById(id).orElseThrow { NoSuchElementException("Statement $id not found") }

    private fun validate(data: StatementFormData): ValidatedStatement {
        val description = data.description.trim()
        require(description.isNotEmpty()) { "Description is required" }
        val date = requireNotNull(data.date) { "Date is required" }
        val amount = requireNotNull(data.amount) { "Amount is required" }
        val categoryId = requireNotNull(data.categoryId) { "Category is required" }
        val accountId = requireNotNull(data.accountId) { "Account is required" }
        val category = categoryRepository.findById(categoryId)
            .orElseThrow { NoSuchElementException("Category $categoryId not found") }
        val account = accountRepository.findById(accountId)
            .orElseThrow { NoSuchElementException("Account $accountId not found") }
        return ValidatedStatement(date, description, category, account, amount)
    }
}
