package paa.myfinanceregister.domain.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import paa.myfinanceregister.domain.model.Account

@SpringBootTest
@Transactional
class AccountRepositoryTests @Autowired constructor(
    private val repository: AccountRepository
) {
    @Test
    fun persistsAccountsAndOrdersThemByDescriptionIncludingInactiveAccounts() {
        repository.save(Account(description = "Wallet", active = false))
        repository.save(Account(description = "Checking"))

        val accounts = repository.findAllByOrderByDescriptionAsc()

        assertThat(accounts.map { it.description }).containsExactly("Checking", "Wallet")
        assertThat(accounts.map { it.active }).containsExactly(true, false)
    }
}
