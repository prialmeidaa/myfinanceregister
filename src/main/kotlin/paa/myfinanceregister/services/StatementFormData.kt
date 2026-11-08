package paa.myfinanceregister.services

import paa.myfinanceregister.domain.model.Account
import paa.myfinanceregister.domain.model.Category
import paa.myfinanceregister.domain.model.StatementTransactionType
import java.math.BigDecimal
import java.time.LocalDate

data class StatementFormData(
    val date: LocalDate?,
    val description: String,
    val categoryId: Long?,
    val accountId: Long?,
    val amount: BigDecimal?,
    val transactionType: StatementTransactionType = StatementTransactionType.SINGLE,
    val installmentNumber: Int? = null,
    val installmentCount: Int? = null,
    val updateFuture: Boolean = false
)

internal data class ValidatedStatement(
    val date: LocalDate,
    val description: String,
    val category: Category,
    val account: Account,
    val amount: BigDecimal
)
