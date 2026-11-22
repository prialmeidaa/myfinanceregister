package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import paa.myfinanceregister.domain.model.Statement

internal object StatementGridStyling {
    fun configure(
        grid: Grid<StatementGridRow>,
        onEdit: (StatementGridRow) -> Unit,
        onDelete: (Statement) -> Unit
    ) {
        grid.addComponentColumn { row ->
            if (row.statement == null) HorizontalLayout() else actionButtons(row, onEdit, onDelete)
        }.addTotalPart().setHeader("Actions").setKey("actions")
            .setWidth(ACTIONS_COLUMN_WIDTH).setFlexGrow(0)
        grid.addColumn(StatementGridRow::date).addTotalPart()
            .setHeader("Date").setWidth(DATE_COLUMN_WIDTH).setFlexGrow(0)
        grid.addColumn(StatementGridRow::description).addTotalPart()
            .setHeader("Description").setAutoWidth(true)
        grid.addColumn(StatementGridRow::category).addTotalPart()
            .setHeader("Category").setAutoWidth(true)
        grid.addColumn(StatementGridRow::account)
            .setPartNameGenerator(::accountCellParts)
            .setHeader("Account").setAutoWidth(true)
        grid.addColumn { row -> StatementGridRowFactory.formatAmount(row.amount) }
            .setHeader("Amount")
            .setWidth(AMOUNT_COLUMN_WIDTH)
            .setFlexGrow(0)
            .setPartNameGenerator(::amountCellParts)
        grid.setSizeFull()
    }

    private fun actionButtons(
        row: StatementGridRow,
        onEdit: (StatementGridRow) -> Unit,
        onDelete: (Statement) -> Unit
    ): HorizontalLayout {
        val statement = requireNotNull(row.statement)
        val description = statement.description
        return HorizontalLayout(
            Button(VaadinIcon.PENCIL.create().apply { setSize(ACTION_ICON_SIZE) }) { onEdit(row) }
                .apply {
                    setAriaLabel("Edit $description")
                    setTooltipText("Edit $description")
                    addThemeVariants(ButtonVariant.TERTIARY, ButtonVariant.SMALL)
                },
            Button(VaadinIcon.CLOSE.create().apply { setSize(ACTION_ICON_SIZE) }) { onDelete(statement) }
                .apply {
                    setAriaLabel("Delete $description")
                    setTooltipText("Delete $description")
                    addThemeVariants(ButtonVariant.TERTIARY, ButtonVariant.SMALL)
                }
        ).apply { setSpacing(false) }
    }

    fun amountCellParts(row: StatementGridRow): String {
        val amountPart = when (row.amount.signum()) {
            -1 -> "statement-amount-negative"
            1 -> "statement-amount-positive"
            else -> "statement-amount-neutral"
        }
        return listOfNotNull(totalCellPart(row), amountPart).joinToString(" ")
    }

    fun accountCellParts(row: StatementGridRow): String =
        listOfNotNull(
            totalCellPart(row),
            if (row.totalType == StatementTotalType.DAILY) "statement-total-label" else null
        ).joinToString(" ")

    private fun Grid.Column<StatementGridRow>.addTotalPart(): Grid.Column<StatementGridRow> =
        setPartNameGenerator { row -> totalCellPart(row) }

    private fun totalCellPart(row: StatementGridRow): String? =
        when (row.totalType) {
            StatementTotalType.DAILY -> "statement-total"
            null -> null
        }

    private const val ACTION_ICON_SIZE = "14px"
    private const val ACTIONS_COLUMN_WIDTH = "100px"
    private const val DATE_COLUMN_WIDTH = "120px"
    private const val AMOUNT_COLUMN_WIDTH = "140px"
}
