package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.confirmdialog.ConfirmDialog
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.grid.ColumnTextAlign
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.treegrid.TreeGrid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll
import paa.myfinanceregister.domain.model.Category
import paa.myfinanceregister.services.CategoryService
import paa.myfinanceregister.ui.components.AppNotification
import paa.myfinanceregister.ui.components.CategoryFormDialog
import paa.myfinanceregister.ui.components.ViewTitle
import paa.myfinanceregister.ui.layout.MainLayout

@Route(value = "categories", layout = MainLayout::class)
@PageTitle("Categories")
@PermitAll
@CssImport("./styles/view-title.css")
@CssImport("./styles/categories-view.css")
class CategoriesView(private val service: CategoryService) : VerticalLayout() {
    private val grid = TreeGrid<Category>()

    init {
        addClassName("categories-view")
        configureGrid()
        val title = ViewTitle("Categories")
        val actions = HorizontalLayout(
            title,
            Button("New category") { openForm() }.apply {
                addThemeVariants(ButtonVariant.PRIMARY)
            }
        ).apply {
            setWidthFull()
            justifyContentMode = FlexComponent.JustifyContentMode.END
            setFlexGrow(1.0, title)
            element.style.set("flex-wrap", "wrap")
            addClassName("view-header")
            addClassName("categories-view-header")
        }
        add(actions, grid)
        refreshGrid()
    }

    private fun configureGrid() {
        grid.addThemeVariants(GridVariant.NO_BORDER, GridVariant.ROW_STRIPES)
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
        grid.setEmptyStateText("No categories have been created")
    }

    private fun createActions(category: Category): Component = HorizontalLayout(
        Button(VaadinIcon.PENCIL.create().apply { setSize(ACTION_ICON_SIZE) }) { openForm(category) }
            .apply {
                setAriaLabel("Edit")
                setTooltipText("Edit")
                addThemeVariants(ButtonVariant.TERTIARY)
            },
        Button(VaadinIcon.CLOSE.create().apply { setSize(ACTION_ICON_SIZE) }) { confirmDelete(category) }
            .apply {
                setAriaLabel("Delete")
                setTooltipText("Delete")
                addThemeVariants(ButtonVariant.ERROR, ButtonVariant.TERTIARY)
            }
    ).apply {
        setSpacing(false)
        addClassName("category-actions")
    }

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
        val dialog = ConfirmDialog().apply {
            setHeader("Delete category?")
            setText(
                "Delete '${category.description}'? Categories used by statements or with subcategories cannot be deleted."
            )
            setCancelable(true)
            setCancelText("Cancel")
            setConfirmText("Delete")
            setConfirmButtonTheme("error primary")
            addConfirmListener { deleteCategory(category) }
        }
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
    }

    private companion object {
        const val ACTION_ICON_SIZE = "16px"
    }
}
