package paa.myfinanceregister.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import java.math.BigDecimal
import java.time.LocalDate

enum class StatementTransactionType {
    SINGLE,
    RECURRING,
    INSTALLMENT
}

enum class StatementKind {
    INCOME,
    EXPENSE,
    TRANSFER
}

@Entity
class Statement(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(nullable = false)
    var date: LocalDate = LocalDate.now(),
    @Column(nullable = false)
    var description: String = "",
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var category: Category? = Category(),
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    var account: Account = Account(),
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_account_id")
    var destinationAccount: Account? = null,
    @Column(nullable = false, precision = 19, scale = 2)
    var amount: BigDecimal = BigDecimal.ZERO,
    @Enumerated(EnumType.STRING)
    var kind: StatementKind? = null,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var transactionType: StatementTransactionType = StatementTransactionType.SINGLE,
    var seriesId: String? = null,
    var seriesIndex: Int? = null,
    var seriesCount: Int? = null
)
