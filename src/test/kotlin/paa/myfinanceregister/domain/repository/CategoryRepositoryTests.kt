package paa.myfinanceregister.domain.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import paa.myfinanceregister.domain.model.Category

@SpringBootTest
@Transactional
class CategoryRepositoryTests @Autowired constructor(
    private val repository: CategoryRepository
) {
    @Test
    fun savesCategoriesWithAnOptionalRootCategory() {
        val root = repository.save(Category(description = "Expenses"))
        repository.save(Category(description = "Food", rootCategory = root))

        val categories = repository.findAllByOrderByDescriptionAsc()

        assertThat(categories.map { it.description }).containsExactly("Expenses", "Food")
        assertThat(categories.last().rootCategory?.id).isEqualTo(root.id)
        assertThat(repository.existsByRootCategoryId(requireNotNull(root.id))).isTrue()
    }
}
