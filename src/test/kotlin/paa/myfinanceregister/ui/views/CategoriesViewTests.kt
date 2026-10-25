package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.treegrid.TreeGrid
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import paa.myfinanceregister.domain.model.Category
import paa.myfinanceregister.services.CategoryService
import paa.myfinanceregister.ui.layout.MainLayout

class CategoriesViewTests {
    @Test
    fun categoriesViewLoadsCategoriesAsATree() {
        val service = Mockito.mock(CategoryService::class.java)
        val root = Category(id = 1, description = "Root")
        val child = Category(id = 2, description = "Child", rootCategory = root)
        Mockito.`when`(service.findAll()).thenReturn(listOf(root, child))
        val view = CategoriesView(service)
        val route = CategoriesView::class.java.getAnnotation(Route::class.java)
        val title = CategoriesView::class.java.getAnnotation(PageTitle::class.java)
        val treeGrid = view.children.toList().filterIsInstance<TreeGrid<Category>>().single()

        assertThat(route.value).isEqualTo("categories")
        assertThat(route.layout.java).isEqualTo(MainLayout::class.java)
        assertThat(title.value).isEqualTo("Categories")
        assertThat(CategoriesView::class.java.isAnnotationPresent(PermitAll::class.java)).isTrue()
        assertThat(treeGrid.dataProvider.hasChildren(root)).isTrue()
        assertThat(treeGrid.dataProvider.hasChildren(child)).isFalse()
        Mockito.verify(service).findAll()
    }

    @Test
    fun failedCategoryDeletionShowsNotificationInPageCenter() {
        val service = Mockito.mock(CategoryService::class.java)
        val category = Category(id = 1, description = "Root")
        val view = CategoriesView(service)
        Mockito.doThrow(IllegalStateException("Cannot delete a category that has subcategories"))
            .`when`(service).delete(1)

        Mockito.mockStatic(Notification::class.java).use { notifications ->
            view.deleteCategory(category)

            notifications.verify {
                Notification.show(
                    "Cannot delete a category that has subcategories",
                    5000,
                    Notification.Position.MIDDLE
                )
            }
        }
    }
}
