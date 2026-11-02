package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.ColumnTextAlign
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.treegrid.TreeGrid
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll
import paa.myfinanceregister.domain.model.Category
import paa.myfinanceregister.services.CategoryService
import paa.myfinanceregister.ui.components.AppNotification
import paa.myfinanceregister.ui.components.CategoryFormDialog
import paa.myfinanceregister.ui.layout.MainLayout

@Route(value = "categories", layout = MainLayout::class)
@PageTitle("Categories")
@PermitAll
class CategoriesView(private val service: CategoryService) : VerticalLayout() {
    private val grid = TreeGrid<Category>()

    init {
        configureGrid()
        val actions = HorizontalLayout(Button("New category") { openForm() }).apply {
            setWidthFull()
            justifyContentMode = FlexComponent.JustifyContentMode.END
        }
        add(actions, grid)
        refreshGrid()
    }

    private fun configureGrid() {
        grid.addComponentColumn(::createActions).setHeader("Actions").setKey("actions")
        grid.addHierarchyColumn(Category::description)
            .setHeader(
                Span("Description").apply {
                    style.set("margin-inline-start", "calc(var(--vaadin-icon-size, 1lh) + var(--vaadin-gap-s))")
                }
            )
            .setAutoWidth(true)
            .setTextAlign(ColumnTextAlign.LEFT)
        grid.setSizeFull()
    }

    private fun createActions(category: Category): Component = HorizontalLayout(
        Button(VaadinIcon.PENCIL.create().apply { setSize(ACTION_ICON_SIZE) }) { openForm(category) }
            .apply {
                setAriaLabel("Edit")
                setTooltipText("Edit")
            },
        Button(VaadinIcon.CLOSE.create().apply { setSize(ACTION_ICON_SIZE) }) { confirmDelete(category) }
            .apply {
                setAriaLabel("Delete")
                setTooltipText("Delete")
            }
    )

    private fun openForm(category: Category? = null) {
        val dialog = CategoryFormDialog(service.findAll(), category, ::saveCategory)
        dialog.open()
    }

    private fun saveCategory(id: Long?, description: String, rootId: Long?): Boolean {
        return try {
            service.save(id, description, rootId)
            refreshGrid()
            AppNotification.show("Category saved")
            true
        } catch (exception: IllegalArgumentException) {
            AppNotification.show(exception.message ?: "Invalid category")
            false
        } catch (exception: NoSuchElementException) {
            AppNotification.show(exception.message ?: "Category not found")
            false
        }
    }

    private fun confirmDelete(category: Category) {
        val dialog = Dialog()
        dialog.add(
            Paragraph("Delete category '${category.description}'?"),
            HorizontalLayout(
                Button("Cancel") { dialog.close() },
                Button("Delete") {
                    deleteCategory(category)
                    dialog.close()
                }
            )
        )
        dialog.open()
    }

    internal fun deleteCategory(category: Category) {
        try {
            service.delete(requireNotNull(category.id))
            refreshGrid()
            AppNotification.show("Category deleted")
        } catch (exception: IllegalStateException) {
            AppNotification.show(
                exception.message ?: "Category cannot be deleted"
            )
        } catch (exception: NoSuchElementException) {
            AppNotification.show(exception.message ?: "Category not found")
        }
    }

    private fun refreshGrid() {
        val categories = service.findAll()
        val childrenByRoot = categories.filter { it.rootCategory != null }.groupBy { it.rootCategory }
        val roots = categories.filter { it.rootCategory == null }
        grid.setItems(roots) { category -> childrenByRoot[category].orEmpty() }
        grid.expandRecursively(roots, categories.size)
    }

    private companion object {
        const val ACTION_ICON_SIZE = "14px"
    }
}
