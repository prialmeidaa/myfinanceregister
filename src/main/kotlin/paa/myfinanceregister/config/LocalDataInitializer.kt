package paa.myfinanceregister.config

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import paa.myfinanceregister.domain.model.Account
import paa.myfinanceregister.domain.model.Category
import paa.myfinanceregister.domain.model.Statement
import paa.myfinanceregister.domain.repository.AccountRepository
import paa.myfinanceregister.domain.repository.CategoryRepository
import paa.myfinanceregister.domain.repository.StatementRepository
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth

@Component
@Profile("dev")
class LocalDataInitializer(
    private val categoryRepository: CategoryRepository,
    private val accountRepository: AccountRepository,
    private val statementRepository: StatementRepository
) : ApplicationRunner {
    @Transactional
    override fun run(args: ApplicationArguments) {
        if (hasExistingData()) return

        val categories = CATEGORY_DESCRIPTIONS.associateWith {
            categoryRepository.save(Category(description = it))
        }
        val accounts = ACCOUNT_DESCRIPTIONS.associateWith {
            accountRepository.save(Account(description = it))
        }
        statementRepository.saveAll(LOCAL_STATEMENTS.map { seed ->
            Statement(
                date = YearMonth.now().minusMonths(seed.monthsAgo).atDay(seed.day),
                description = seed.description,
                category = requireNotNull(categories[seed.category]),
                account = requireNotNull(accounts[seed.account]),
                amount = BigDecimal(seed.amount)
            )
        })
    }

    private fun hasExistingData() =
        categoryRepository.count() > 0 || accountRepository.count() > 0 || statementRepository.count() > 0

    private data class LocalStatementSeed(
        val description: String,
        val category: String,
        val account: String,
        val amount: String,
        val monthsAgo: Long,
        val day: Int
    )

    private companion object {
        val CATEGORY_DESCRIPTIONS = listOf("Food", "Housing", "Transportation", "Health", "Entertainment")
        val ACCOUNT_DESCRIPTIONS =
            listOf("Checking Account", "Savings Account", "Wallet", "Primary Credit Card", "Investments")
        val LOCAL_STATEMENTS = listOf(
            LocalStatementSeed("Monthly groceries", "Food", "Checking Account", "685.40", 2, 5),
            LocalStatementSeed("Rent", "Housing", "Checking Account", "1450.00", 2, 8),
            LocalStatementSeed("Transportation", "Transportation", "Wallet", "92.50", 2, 18),
            LocalStatementSeed("Medical appointment", "Health", "Primary Credit Card", "180.00", 1, 6),
            LocalStatementSeed("Movie theater", "Entertainment", "Primary Credit Card", "68.00", 1, 12),
            LocalStatementSeed("Electricity bill", "Housing", "Checking Account", "215.30", 1, 20),
            LocalStatementSeed("Supermarket", "Food", "Savings Account", "523.75", 0, 3),
            LocalStatementSeed("Gym membership", "Health", "Checking Account", "89.90", 0, 9),
            LocalStatementSeed("Monthly investment", "Entertainment", "Investments", "300.00", 0, 16),
            LocalStatementSeed("Restaurant", "Food", "Primary Credit Card", "74.50", 0, 24),
            LocalStatementSeed("Returned purchase", "Food", "Checking Account", "-45.00", 0, 3),
            LocalStatementSeed("Service refund", "Transportation", "Wallet", "-25.00", 0, 18)
        )
    }
}
