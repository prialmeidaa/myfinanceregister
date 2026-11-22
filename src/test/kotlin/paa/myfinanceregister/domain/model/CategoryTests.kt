package paa.myfinanceregister.domain.model

import jakarta.persistence.Column
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CategoryTests {
    @Test
    fun descriptionColumnIsLimitedToOneHundredCharacters() {
        val column = Category::class.java.getDeclaredField("description")
            .getAnnotation(Column::class.java)

        assertThat(column.length).isEqualTo(Category.MAX_DESCRIPTION_LENGTH)
        assertThat(Category.MAX_DESCRIPTION_LENGTH).isEqualTo(100)
    }
}
