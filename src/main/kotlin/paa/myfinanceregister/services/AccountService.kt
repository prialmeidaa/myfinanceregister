package paa.myfinanceregister.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import paa.myfinanceregister.domain.model.Account
import paa.myfinanceregister.domain.repository.AccountRepository

@Service
class AccountService(private val repository: AccountRepository) {
    @Transactional(readOnly = true)
    fun findAll(): List<Account> = repository.findAllByOrderByDescriptionAsc()

    @Transactional
    fun save(id: Long?, description: String): Account {
        val normalizedDescription = description.trim()
        require(normalizedDescription.isNotEmpty()) { "Description is required" }

        val account = id?.let(::loadAccount) ?: Account()
        account.description = normalizedDescription
        return repository.save(account)
    }

    @Transactional
    fun deactivate(id: Long): Account {
        val account = loadAccount(id)
        account.active = false
        return repository.save(account)
    }

    @Transactional
    fun activate(id: Long): Account {
        val account = loadAccount(id)
        account.active = true
        return repository.save(account)
    }

    private fun loadAccount(id: Long): Account =
        repository.findById(id).orElseThrow { NoSuchElementException("Account $id not found") }
}
