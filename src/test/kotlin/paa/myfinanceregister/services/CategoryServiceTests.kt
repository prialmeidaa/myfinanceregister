package paa.myfinanceregister.services

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import paa.myfinanceregister.domain.model.Category
import paa.myfinanceregister.domain.repository.CategoryRepository
import paa.myfinanceregister.domain.repository.StatementRepository
import java.util.Optional

class CategoryServiceTests {
    private val repository = Mockito.mock(CategoryRepository::class.java)
    private val statementRepository = Mockito.mock(StatementRepository::class.java)
    private val service = CategoryService(repository, statementRepository)

    @Test
    fun findAllReturnsCategoriesInRepositoryOrder() {
        val categories = listOf(Category(1L, "Food"), Category(2L, "Housing"))
        Mockito.`when`(repository.findAllByOrderByDescriptionAsc()).thenReturn(categories)

        assertThat(service.findAll()).containsExactlyElementsOf(categories)
    }

    @Test
    fun saveTrimsDescriptionAndAssociatesRootCategory() {
        val root = Category(1L, "Expenses")
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(root))
        Mockito.`when`(repository.save(Mockito.any(Category::class.java))).thenAnswer { it.arguments[0] }

        val saved = service.save(null, "  Food  ", 1L)

        assertThat(saved.description).isEqualTo("Food")
        assertThat(saved.rootCategory).isSameAs(root)
    }

    @Test
    fun saveRejectsBlankDescription() {
        assertThatThrownBy { service.save(null, "   ", null) }
            .isInstanceOf(IllegalArgumentException::class.java)

        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Category::class.java))
    }

    @Test
    fun saveRejectsCategoryAsItsOwnRoot() {
        val category = Category(1L, "Food")
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(category))

        assertThatThrownBy { service.save(1L, "Food", 1L) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun saveRejectsRootThatIsAlreadyADescendant() {
        val category = Category(1L, "Expenses")
        val child = Category(2L, "Food", category)
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(category))
        Mockito.`when`(repository.findById(2L)).thenReturn(Optional.of(child))

        assertThatThrownBy { service.save(1L, "Expenses", 2L) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun deleteRejectsCategoryWithSubcategories() {
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(Category(1L, "Expenses")))
        Mockito.`when`(repository.existsByRootCategoryId(1L)).thenReturn(true)

        assertThatThrownBy { service.delete(1L) }
            .isInstanceOf(IllegalStateException::class.java)

        Mockito.verify(repository, Mockito.never()).delete(Mockito.any(Category::class.java))
    }

    @Test
    fun deleteRejectsCategoryUsedByStatements() {
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(Category(1L, "Food")))
        Mockito.`when`(statementRepository.existsByCategoryId(1L)).thenReturn(true)

        assertThatThrownBy { service.delete(1L) }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Cannot delete a category that is used by statements")

        Mockito.verify(repository, Mockito.never()).delete(Mockito.any(Category::class.java))
    }

    @Test
    fun deleteRemovesCategoryWithoutSubcategories() {
        val category = Category(1L, "Food")
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(category))

        service.delete(1L)

        Mockito.verify(repository).delete(category)
    }
}
