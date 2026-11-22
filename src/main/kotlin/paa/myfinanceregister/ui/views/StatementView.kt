package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.combobox.MultiSelectComboBox
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll
import paa.myfinanceregister.domain.model.Account
import paa.myfinanceregister.domain.model.Category
import paa.myfinanceregister.domain.model.Statement
import paa.myfinanceregister.services.AccountService
import paa.myfinanceregister.services.CategoryService
import paa.myfinanceregister.services.StatementFormData
import paa.myfinanceregister.services.StatementService
import paa.myfinanceregister.ui.components.AppNotification
import paa.myfinanceregister.ui.components.StatementFormDialog
import paa.myfinanceregister.ui.components.StatementMonthFilter
import paa.myfinanceregister.ui.components.ViewTitle
import paa.myfinanceregister.ui.layout.MainLayout

@Route(value = "statement", layout = MainLayout::class)
@PageTitle("Statement")
@PermitAll
@CssImport("./styles/statement-view.css")
@CssImport("./styles/view-title.css")
class StatementView(
    private val service: StatementService,
    private val categoryService: CategoryService,
    private val accountService: AccountService
) : VerticalLayout() {
    private val grid = Grid<StatementGridRow>()
    private val monthFilter = StatementMonthFilter(::refreshGrid)
    private val categories = categoryService.findAll()
    private val accounts = accountService.findAll()
    private val categoryFilter = MultiSelectComboBox<Category>("Category").apply {
        setItems(categories)
        setItemLabelGenerator { it.description }
        placeholder = "All categories"
        setClearButtonVisible(true)
        addValueChangeListener { refreshGrid() }
    }
    private val accountFilter = MultiSelectComboBox<Account>("Account").apply {
        setItems(accounts)
        setItemLabelGenerator { it.description }
        placeholder = "All accounts"
        setClearButtonVisible(true)
        addValueChangeListener { refreshGrid() }
    }

    init {
        StatementGridStyling.configure(grid, ::toggleInlineEditor, ::confirmDelete)
        StatementGridDetails.configure(
            grid,
            StatementGridDetailsConfiguration(
                categories,
                accounts,
                ::saveStatement
            )
        )
        val title = ViewTitle("Statement")
        val actions = HorizontalLayout(
            title,
            Button("New statement") { openForm() }.apply {
                addThemeVariants(ButtonVariant.PRIMARY)
            }
        ).apply {
            setWidthFull()
            setWrap(true)
            setAlignItems(FlexComponent.Alignment.CENTER)
            setFlexGrow(1.0, title)
            addClassName("view-header")
        }
        val filters = HorizontalLayout(monthFilter, categoryFilter, accountFilter).apply {
            setWidthFull()
            setWrap(true)
            setAlignItems(FlexComponent.Alignment.CENTER)
        }
        add(actions, filters, grid)
        refreshGrid()
    }

    private fun openForm(statement: Statement? = null) {
        StatementFormDialog(
            categoryService.findAll(),
            accountService.findAll(),
            statement,
            ::saveStatement
        ).open()
    }

    private fun toggleInlineEditor(row: StatementGridRow) {
        grid.setDetailsVisible(row, !grid.isDetailsVisible(row))
    }

    private fun saveStatement(id: Long?, data: StatementFormData): Boolean {
        return try {
            service.save(id, data)
            refreshGrid()
            AppNotification.show("Statement saved")
            true
        } catch (exception: IllegalArgumentException) {
            AppNotification.show(exception.message ?: "Invalid statement")
            false
        } catch (exception: NoSuchElementException) {
            AppNotification.show(exception.message ?: "Statement reference not found")
            false
        }
    }

    private fun confirmDelete(statement: Statement) {
        val dialog = Dialog()
        dialog.setHeaderTitle("Delete statement")
        dialog.setCloseOnEsc(true)
        dialog.setCloseOnOutsideClick(false)
        dialog.add(
            Paragraph("Delete '${statement.description}' from ${statement.date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))}?"),
            HorizontalLayout(
                Button("Cancel") { dialog.close() }.apply {
                    addThemeVariants(ButtonVariant.TERTIARY)
                },
                Button("Delete") {
                    deleteStatement(statement)
                    dialog.close()
                }.apply {
                    addThemeVariants(ButtonVariant.ERROR)
                }
            )
        )
        dialog.open()
    }

    internal fun deleteStatement(statement: Statement) {
        try {
            service.delete(requireNotNull(statement.id))
            refreshGrid()
            AppNotification.show("Statement deleted")
        } catch (exception: NoSuchElementException) {
            AppNotification.show(exception.message ?: "Statement not found")
        }
    }

    private fun refreshGrid() {
        val statements = service.findAll()
        monthFilter.updateMonths(statements)
        val filteredStatements = statements.filterByCategory().filterByAccount()
        grid.setItems(StatementGridRowFactory.create(monthFilter.filter(filteredStatements)))
    }

    private fun List<Statement>.filterByCategory(): List<Statement> {
        val selectedCategoryIds = categoryFilter.value.mapNotNull { it.id }.toMutableSet()
        if (selectedCategoryIds.isEmpty()) return this
        addDescendantCategoryIds(selectedCategoryIds)
        return filter { statement ->
            statement.category?.id in selectedCategoryIds
        }
    }

    private fun List<Statement>.filterByAccount(): List<Statement> {
        val selectedAccountIds = accountFilter.value.mapNotNull { it.id }.toSet()
        if (selectedAccountIds.isEmpty()) return this
        return filter {
            it.account.id in selectedAccountIds || it.destinationAccount?.id in selectedAccountIds
        }
    }

    private fun addDescendantCategoryIds(selectedCategoryIds: MutableSet<Long>) {
        var foundDescendant: Boolean
        do {
            val descendantIds = categories
                .filter { it.rootCategory?.id in selectedCategoryIds }
                .mapNotNull { it.id }
            foundDescendant = selectedCategoryIds.addAll(descendantIds)
        } while (foundDescendant)
    }
}
