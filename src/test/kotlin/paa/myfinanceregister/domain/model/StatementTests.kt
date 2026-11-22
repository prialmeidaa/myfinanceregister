package paa.myfinanceregister.domain.model

import jakarta.persistence.Column
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class StatementTests {
    @Test
    fun descriptionColumnIsLimitedToOneHundredCharacters() {
        val column = Statement::class.java.getDeclaredField("description")
            .getAnnotation(Column::class.java)

        assertThat(column.length).isEqualTo(Statement.MAX_DESCRIPTION_LENGTH)
    }

    @Test
    fun createsStatementWithDateDescriptionCategoryAccountAndAmount() {
        val date = LocalDate.of(2026, 10, 28)
        val category = Category(1, "Food")
        val account = Account(2, "Checking")

        val statement = Statement(
            date = date,
            description = "Lunch",
            category = category,
            account = account,
            amount = BigDecimal("25.50")
        )

        assertThat(statement.date).isEqualTo(date)
        assertThat(statement.description).isEqualTo("Lunch")
        assertThat(statement.category).isSameAs(category)
        assertThat(statement.account).isSameAs(account)
        assertThat(statement.amount).isEqualByComparingTo("25.50")
    }
}
