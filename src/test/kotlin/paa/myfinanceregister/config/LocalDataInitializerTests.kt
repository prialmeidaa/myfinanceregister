package paa.myfinanceregister.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.DefaultApplicationArguments
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import paa.myfinanceregister.domain.repository.AccountRepository
import paa.myfinanceregister.domain.repository.CategoryRepository
import paa.myfinanceregister.domain.repository.StatementRepository
import java.time.YearMonth

@SpringBootTest
@ActiveProfiles("dev")
class LocalDataInitializerTests @Autowired constructor(
    private val initializer: LocalDataInitializer,
    private val categoryRepository: CategoryRepository,
    private val accountRepository: AccountRepository,
    private val statementRepository: StatementRepository
) {
    @Test
    fun loadsInitialLocalDataAcrossAtLeastThreeMonthsIncludingCurrentMonth() {
        val statements = statementRepository.findAllByOrderByDateDesc()
        val statementMonths = statements.map { YearMonth.from(it.date) }.distinct()

        assertThat(categoryRepository.count()).isGreaterThanOrEqualTo(5)
        assertThat(accountRepository.count()).isGreaterThanOrEqualTo(5)
        assertThat(statements).hasSizeGreaterThanOrEqualTo(10)
        assertThat(statementMonths).hasSizeGreaterThanOrEqualTo(3)
        assertThat(statementMonths.max()).isEqualTo(YearMonth.now())
    }

    @Test
    fun doesNotDuplicateDataWhenInitializedAgain() {
        val initialCounts = listOf(
            categoryRepository.count(),
            accountRepository.count(),
            statementRepository.count()
        )

        initializer.run(DefaultApplicationArguments())

        assertThat(categoryRepository.count()).isEqualTo(initialCounts[0])
        assertThat(accountRepository.count()).isEqualTo(initialCounts[1])
        assertThat(statementRepository.count()).isEqualTo(initialCounts[2])
    }
}
