package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import paa.myfinanceregister.domain.model.Statement

internal object StatementGridStyling {
    fun configure(
        grid: Grid<StatementGridRow>,
        onEdit: (Statement) -> Unit,
        onDelete: (Statement) -> Unit
    ) {
        grid.addComponentColumn { row ->
            val statement = row.statement ?: return@addComponentColumn HorizontalLayout()
            HorizontalLayout(
                Button(VaadinIcon.PENCIL.create().apply { setSize(ACTION_ICON_SIZE) }) { onEdit(statement) }
                    .apply {
                        setAriaLabel("Edit")
                        setTooltipText("Edit")
                    },
                Button(VaadinIcon.CLOSE.create().apply { setSize(ACTION_ICON_SIZE) }) { onDelete(statement) }
                    .apply {
                        setAriaLabel("Delete")
                        setTooltipText("Delete")
                    }
            )
        }.addTotalPart().setHeader("Actions").setKey("actions")
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
            null -> null
        }

    private const val ACTION_ICON_SIZE = "14px"
}
