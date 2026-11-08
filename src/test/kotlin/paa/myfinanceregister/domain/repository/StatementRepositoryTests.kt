package paa.myfinanceregister.domain.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import paa.myfinanceregister.domain.model.Account
import paa.myfinanceregister.domain.model.Category
import paa.myfinanceregister.domain.model.Statement
import paa.myfinanceregister.domain.model.StatementKind
import java.math.BigDecimal
import java.time.LocalDate

@SpringBootTest
@Transactional
class StatementRepositoryTests @Autowired constructor(
    private val repository: StatementRepository,
    private val categoryRepository: CategoryRepository,
    private val accountRepository: AccountRepository
) {
    @Test
    fun persistsStatementReferencesAndOrdersByDateDescending() {
        val category = categoryRepository.save(Category(description = "Food"))
        val account = accountRepository.save(Account(description = "Checking"))
        val older = repository.save(createStatement(LocalDate.of(2026, 9, 1), category, account, "12.00"))
        val newer = repository.save(createStatement(LocalDate.of(2026, 10, 1), category, account, "30.00"))

        val statements = repository.findAllByOrderByDateDesc()
            .filter { it.id == older.id || it.id == newer.id }

        assertThat(statements.map { it.date }).containsExactly(
            LocalDate.of(2026, 10, 1),
            LocalDate.of(2026, 9, 1)
        )
        assertThat(statements.first().category?.description).isEqualTo("Food")
        assertThat(statements.first().account.description).isEqualTo("Checking")
        assertThat(statements.first().amount).isEqualByComparingTo("30.00")
    }

    @Test
    fun detectsWhetherCategoryIsUsedByAStatement() {
        val category = categoryRepository.save(Category(description = "Food"))
        val unusedCategory = categoryRepository.save(Category(description = "Housing"))
        val account = accountRepository.save(Account(description = "Checking"))
        repository.save(createStatement(LocalDate.of(2026, 9, 1), category, account, "12.00"))

        assertThat(repository.existsByCategoryId(requireNotNull(category.id))).isTrue()
        assertThat(repository.existsByCategoryId(requireNotNull(unusedCategory.id))).isFalse()
    }

    @Test
    fun persistsTransferWithoutCategoryAndWithDestinationAccount() {
        val origin = accountRepository.save(Account(description = "Checking"))
        val destination = accountRepository.save(Account(description = "Savings"))
        val transfer = repository.save(
            Statement(
                description = "Move savings",
                category = null,
                account = origin,
                destinationAccount = destination,
                amount = BigDecimal("25.00"),
                kind = StatementKind.TRANSFER
            )
        )

        val loaded = repository.findById(requireNotNull(transfer.id)).orElseThrow()

        assertThat(loaded.id).isEqualTo(transfer.id)
        assertThat(loaded.category).isNull()
        assertThat(loaded.account.description).isEqualTo("Checking")
        assertThat(loaded.destinationAccount?.description).isEqualTo("Savings")
        assertThat(loaded.kind).isEqualTo(StatementKind.TRANSFER)
    }

    private fun createStatement(
        date: LocalDate,
        category: Category,
        account: Account,
        amount: String
    ) = Statement(
        date = date,
        description = "Purchase",
        category = category,
        account = account,
        amount = BigDecimal(amount)
    )
}
