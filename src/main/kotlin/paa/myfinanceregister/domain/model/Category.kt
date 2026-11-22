package paa.myfinanceregister.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(nullable = false, length = MAX_DESCRIPTION_LENGTH)
    var description: String = "",
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_category_id")
    var rootCategory: Category? = null
) {
    companion object {
        const val MAX_DESCRIPTION_LENGTH = 100
    }
}
