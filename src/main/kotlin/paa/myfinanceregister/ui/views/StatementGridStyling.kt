package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import paa.myfinanceregister.domain.model.Statement

internal object StatementGridStyling {
    fun configure(
        grid: Grid<StatementGridRow>,
        onEdit: (Statement) -> Unit,
        onDelete: (Statement) -> Unit
    ) {
        grid.addColumn(StatementGridRow::date).addTotalPart()
            .setHeader("Date").setAutoWidth(true)
        grid.addColumn(StatementGridRow::description).addTotalPart()
            .setHeader("Description").setAutoWidth(true)
        grid.addColumn(StatementGridRow::category).addTotalPart()
            .setHeader("Category").setAutoWidth(true)
        grid.addColumn(StatementGridRow::account).addTotalPart()
            .setHeader("Account").setAutoWidth(true)
        grid.addColumn { row -> StatementGridRowFactory.formatAmount(row.amount) }
            .setHeader("Amount")
            .setAutoWidth(true)
            .setPartNameGenerator(::amountCellParts)
        grid.addComponentColumn { row ->
            val statement = row.statement ?: return@addComponentColumn HorizontalLayout()
            HorizontalLayout(
                Button("Edit") { onEdit(statement) },
                Button("Delete") { onDelete(statement) }
            )
        }.addTotalPart().setHeader("Actions")
        grid.setSizeFull()
    }

    fun amountCellParts(row: StatementGridRow): String {
        val amountPart = when (row.amount.signum()) {
            -1 -> "statement-amount-negative"
            1 -> "statement-amount-positive"
            else -> "statement-amount-neutral"
        }
        return listOfNotNull(totalCellPart(row), amountPart).joinToString(" ")
    }

    private fun Grid.Column<StatementGridRow>.addTotalPart(): Grid.Column<StatementGridRow> =
        setPartNameGenerator { row -> totalCellPart(row) }

    private fun totalCellPart(row: StatementGridRow): String? =
        when (row.totalType) {
            StatementTotalType.DAILY -> "statement-total"
            StatementTotalType.MONTHLY -> "statement-month-total"
            null -> null
        }
}
