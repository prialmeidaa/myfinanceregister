package paa.myfinanceregister.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import paa.myfinanceregister.domain.model.Account

interface AccountRepository : JpaRepository<Account, Long> {
    fun findAllByOrderByDescriptionAsc(): List<Account>
}
