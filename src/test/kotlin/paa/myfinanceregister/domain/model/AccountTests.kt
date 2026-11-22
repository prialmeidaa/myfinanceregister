package paa.myfinanceregister.domain.model

import jakarta.persistence.Column
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AccountTests {
    @Test
    fun descriptionColumnIsLimitedToFiftyCharacters() {
        val column = Account::class.java.getDeclaredField("description")
            .getAnnotation(Column::class.java)

        assertThat(column.length).isEqualTo(Account.MAX_DESCRIPTION_LENGTH)
        assertThat(Account.MAX_DESCRIPTION_LENGTH).isEqualTo(50)
    }
}
