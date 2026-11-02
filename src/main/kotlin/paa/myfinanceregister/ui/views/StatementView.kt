package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll
import paa.myfinanceregister.domain.model.Statement
import paa.myfinanceregister.services.AccountService
import paa.myfinanceregister.services.CategoryService
import paa.myfinanceregister.services.StatementFormData
import paa.myfinanceregister.services.StatementService
import paa.myfinanceregister.ui.components.AppNotification
import paa.myfinanceregister.ui.components.StatementFormDialog
import paa.myfinanceregister.ui.components.StatementMonthFilter
import paa.myfinanceregister.ui.layout.MainLayout

@Route(value = "statement", layout = MainLayout::class)
@PageTitle("Statement")
@PermitAll
class StatementView(
    private val service: StatementService,
    private val categoryService: CategoryService,
    private val accountService: AccountService
) : VerticalLayout() {
    private val grid = Grid<StatementGridRow>()
    private val monthFilter = StatementMonthFilter(::refreshGrid)

    init {
        configureGrid()
        add(HorizontalLayout(Button("New statement") { openForm() }, monthFilter), grid)
        refreshGrid()
    }

    private fun configureGrid() {
        grid.addColumn(StatementGridRow::date).setHeader("Date").setAutoWidth(true)
        grid.addColumn(StatementGridRow::description).setHeader("Description").setAutoWidth(true)
        grid.addColumn(StatementGridRow::category).setHeader("Category").setAutoWidth(true)
        grid.addColumn(StatementGridRow::account).setHeader("Account").setAutoWidth(true)
        grid.addColumn { row -> StatementGridRowFactory.formatAmount(row.amount) }
            .setHeader("Amount")
            .setAutoWidth(true)
        grid.addComponentColumn { row ->
            val statement = row.statement ?: return@addComponentColumn HorizontalLayout()
            HorizontalLayout(
                Button("Edit") { openForm(statement) },
                Button("Delete") { confirmDelete(statement) }
            )
        }.setHeader("Actions")
        grid.setSizeFull()
    }

    private fun openForm(statement: Statement? = null) {
        StatementFormDialog(
            categoryService.findAll(),
            accountService.findAll(),
            statement,
            ::saveStatement
        ).open()
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
        dialog.add(
            Paragraph("Delete statement '${statement.description}'?"),
            HorizontalLayout(
                Button("Cancel") { dialog.close() },
                Button("Delete") {
                    deleteStatement(statement)
                    dialog.close()
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
        grid.setItems(StatementGridRowFactory.create(monthFilter.filter(statements)))
    }
}
