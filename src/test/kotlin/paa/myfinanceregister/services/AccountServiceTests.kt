package paa.myfinanceregister.services

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import paa.myfinanceregister.domain.model.Account
import paa.myfinanceregister.domain.repository.AccountRepository
import java.util.Optional

class AccountServiceTests {
    private val repository = Mockito.mock(AccountRepository::class.java)
    private val service = AccountService(repository)

    @Test
    fun findAllReturnsAccountsInRepositoryOrder() {
        val accounts = listOf(Account(1, "Checking"), Account(2, "Wallet"))
        Mockito.`when`(repository.findAllByOrderByDescriptionAsc()).thenReturn(accounts)

        assertThat(service.findAll()).containsExactlyElementsOf(accounts)
    }

    @Test
    fun saveTrimsDescriptionAndCreatesActiveAccount() {
        Mockito.`when`(repository.save(Mockito.any(Account::class.java))).thenAnswer { it.arguments[0] }

        val saved = service.save(null, "  Checking  ")

        assertThat(saved.description).isEqualTo("Checking")
        assertThat(saved.active).isTrue()
    }

    @Test
    fun saveUpdatesAnExistingAccountWithoutReactivatingIt() {
        val account = Account(1, "Old name", active = false)
        Mockito.`when`(repository.findById(1)).thenReturn(Optional.of(account))
        Mockito.`when`(repository.save(account)).thenReturn(account)

        val saved = service.save(1, "  New name ")

        assertThat(saved.description).isEqualTo("New name")
        assertThat(saved.active).isFalse()
    }

    @Test
    fun saveRejectsBlankDescription() {
        assertThatThrownBy { service.save(null, "  ") }
            .isInstanceOf(IllegalArgumentException::class.java)

        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Account::class.java))
    }

    @Test
    fun deactivatePersistsInactiveAccountWithoutDeletingIt() {
        val account = Account(1, "Checking")
        Mockito.`when`(repository.findById(1)).thenReturn(Optional.of(account))
        Mockito.`when`(repository.save(account)).thenReturn(account)

        val deactivated = service.deactivate(1)

        assertThat(deactivated.active).isFalse()
        Mockito.verify(repository).save(account)
        Mockito.verify(repository, Mockito.never()).delete(Mockito.any(Account::class.java))
    }

    @Test
    fun activatePersistsActiveAccountWithoutCreatingAnotherAccount() {
        val account = Account(1, "Checking", active = false)
        Mockito.`when`(repository.findById(1)).thenReturn(Optional.of(account))
        Mockito.`when`(repository.save(account)).thenReturn(account)

        val activated = service.activate(1)

        assertThat(activated.active).isTrue()
        Mockito.verify(repository).save(account)
        Mockito.verify(repository, Mockito.never()).delete(Mockito.any(Account::class.java))
    }
}
