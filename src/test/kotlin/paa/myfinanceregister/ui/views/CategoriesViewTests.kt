package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.ColumnTextAlign
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.data.renderer.ComponentRenderer
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
    fun newCategoryButtonIsAlignedToTheRightOfTheGrid() {
        val view = CategoriesView(Mockito.mock(CategoryService::class.java))
        val actions = view.children.toList().filterIsInstance<HorizontalLayout>().single()

        assertThat(actions.width).isEqualTo("100%")
        assertThat(actions.justifyContentMode).isEqualTo(FlexComponent.JustifyContentMode.END)
        assertThat(actions.children.toList().filterIsInstance<Button>().single().text)
            .isEqualTo("New category")
    }

    @Test
    fun categoryActionsAreFirstAndUseIconOnlyButtons() {
        val service = Mockito.mock(CategoryService::class.java)
        val category = Category(id = 1, description = "Food")
        val grid = CategoriesView(service).children.toList()
            .filterIsInstance<TreeGrid<Category>>()
            .single()
        val column = grid.columns.first()
        val renderer = column.renderer as ComponentRenderer<HorizontalLayout, Category>
        val buttons = renderer.createComponent(category).children.toList().filterIsInstance<Button>()

        assertThat(column.headerText).isEqualTo("Actions")
        assertThat(column.key).isEqualTo("actions")
        assertThat(buttons).hasSize(2)
        assertThat(buttons.map { it.text }).containsExactly("", "")
        assertThat(buttons.map { it.ariaLabel.orElse("") }).containsExactly("Edit", "Delete")
        assertThat(buttons.map { it.tooltip.text }).containsExactly("Edit", "Delete")
        assertThat(buttons.map { (it.icon as Icon).icon })
            .containsExactly(VaadinIcon.PENCIL.create().icon, VaadinIcon.CLOSE.create().icon)
        assertThat(buttons.map { (it.icon as Icon).element.style.get("width") })
            .containsExactly("14px", "14px")
        val descriptionColumn = grid.columns[1]
        val descriptionHeader = descriptionColumn.headerComponent as Span
        assertThat(descriptionHeader.text).isEqualTo("Description")
        assertThat(descriptionHeader.element.style.get("margin-inline-start"))
            .isEqualTo("calc(var(--vaadin-icon-size, 1lh) + var(--vaadin-gap-s))")
        assertThat(descriptionColumn.textAlign).isEqualTo(ColumnTextAlign.LEFT)
    }

    @Test
    fun deletingCategoryUsedByStatementsShowsNotificationInPageCenter() {
        val service = Mockito.mock(CategoryService::class.java)
        val category = Category(id = 1, description = "Root")
        val view = CategoriesView(service)
        Mockito.doThrow(IllegalStateException("Cannot delete a category that is used by statements"))
            .`when`(service).delete(1)

        Mockito.mockStatic(Notification::class.java).use { notifications ->
            view.deleteCategory(category)

            notifications.verify {
                Notification.show(
                    "Cannot delete a category that is used by statements",
                    5000,
                    Notification.Position.TOP_CENTER
                )
            }
        }
    }
}
