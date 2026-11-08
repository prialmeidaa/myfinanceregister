package paa.myfinanceregister.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import paa.myfinanceregister.domain.model.Account
import paa.myfinanceregister.domain.model.Statement
import paa.myfinanceregister.domain.model.StatementKind
import paa.myfinanceregister.domain.repository.AccountRepository
import paa.myfinanceregister.domain.repository.CategoryRepository
import paa.myfinanceregister.domain.repository.StatementRepository

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
        val amount = requireNotNull(data.amount) { "Amount is required" }.abs()
        val accountId = requireNotNull(data.accountId) { "Account is required" }
        val category = findCategory(data)
        val destinationAccount = findDestinationAccount(data)
        val account = accountRepository.findById(accountId)
            .orElseThrow { NoSuchElementException("Account $accountId not found") }
        val signedAmount = if (data.kind == StatementKind.EXPENSE) amount.negate() else amount
        return ValidatedStatement(
            date, description, category, account, destinationAccount, signedAmount, data.kind
        )
    }

    private fun findCategory(data: StatementFormData) =
        if (data.kind == StatementKind.TRANSFER) null else {
            val categoryId = requireNotNull(data.categoryId) { "Category is required" }
            categoryRepository.findById(categoryId)
                .orElseThrow { NoSuchElementException("Category $categoryId not found") }
        }

    private fun findDestinationAccount(data: StatementFormData): Account? =
        if (data.kind != StatementKind.TRANSFER) null else {
            val destinationId = requireNotNull(data.destinationAccountId) { "Destination account is required" }
            require(destinationId != data.accountId) { "Origin and destination accounts must be different" }
            accountRepository.findById(destinationId)
                .orElseThrow { NoSuchElementException("Account $destinationId not found") }
        }
}
