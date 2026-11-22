package paa.myfinanceregister.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(nullable = false, length = MAX_DESCRIPTION_LENGTH)
    var description: String = "",
    @Column(nullable = false)
    var active: Boolean = true
) {
    companion object {
        const val MAX_DESCRIPTION_LENGTH = 50
    }
}
