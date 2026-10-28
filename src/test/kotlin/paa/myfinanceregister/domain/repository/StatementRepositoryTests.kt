package paa.myfinanceregister.domain.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import paa.myfinanceregister.domain.model.Account
import paa.myfinanceregister.domain.model.Category
import paa.myfinanceregister.domain.model.Statement
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
        repository.save(createStatement(LocalDate.of(2026, 9, 1), category, account, "12.00"))
        repository.save(createStatement(LocalDate.of(2026, 10, 1), category, account, "30.00"))

        val statements = repository.findAllByOrderByDateDesc()

        assertThat(statements.map { it.date }).containsExactly(
            LocalDate.of(2026, 10, 1),
            LocalDate.of(2026, 9, 1)
        )
        assertThat(statements.first().category.description).isEqualTo("Food")
        assertThat(statements.first().account.description).isEqualTo("Checking")
        assertThat(statements.first().amount).isEqualByComparingTo("30.00")
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
