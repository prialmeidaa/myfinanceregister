package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.data.renderer.ComponentRenderer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import paa.myfinanceregister.domain.model.Account
import paa.myfinanceregister.domain.model.Category
import paa.myfinanceregister.domain.model.Statement
import paa.myfinanceregister.services.StatementFormData
import paa.myfinanceregister.ui.components.StatementInlineEditor
import java.math.BigDecimal
import java.time.LocalDate

class StatementGridDetailsTests {
    @Test
    fun rendersAnInlineEditorForStatementsAndNoEditorForDailyTotals() {
        val grid = Grid<StatementGridRow>()
        val category = Category(1, "Food")
        val account = Account(2, "Checking")
        val renderer = StatementGridDetails.configure(
            grid,
            StatementGridDetailsConfiguration(listOf(category), listOf(account)) { _, _ -> true }
        )
        val statementRow = StatementGridRow(
            "05/10/2026",
            "Purchase",
            "Food",
            "Checking",
            BigDecimal("10.00"),
            Statement(date = LocalDate.of(2026, 10, 5), category = category, account = account)
        )
        val totalRow = statementRow.copy(statement = null, totalType = StatementTotalType.DAILY)

        assertThat(renderer.createComponent(statementRow)).isInstanceOf(StatementInlineEditor::class.java)
        assertThat(renderer.createComponent(totalRow)).isNotInstanceOf(StatementInlineEditor::class.java)
        assertThat(renderer.createComponent(totalRow).children).isEmpty()
    }

    @Test
    fun detailsAreNotToggledByClickingTheRow() {
        val grid = Grid<StatementGridRow>()

        StatementGridDetails.configure(
            grid,
            StatementGridDetailsConfiguration(emptyList(), emptyList()) { _: Long?, _: StatementFormData -> true }
        )

        assertThat(grid.isDetailsVisibleOnClick).isFalse()
    }

    @Test
    fun editActionTogglesTheSelectedRowsDetails() {
        val grid = Grid<StatementGridRow>()
        val row = StatementGridRow(
            "05/10/2026",
            "Purchase",
            "Food",
            "Checking",
            BigDecimal("10.00"),
            Statement(category = Category(1, "Food"), account = Account(2, "Checking"))
        )
        grid.setItems(row)
        StatementGridDetails.configure(
            grid,
            StatementGridDetailsConfiguration(emptyList(), emptyList()) { _, _ -> true }
        )
        StatementGridStyling.configure(
            grid,
            { selected -> grid.setDetailsVisible(selected, !grid.isDetailsVisible(selected)) },
            {}
        )
        val renderer = grid.columns.first().renderer as ComponentRenderer<HorizontalLayout, StatementGridRow>
        val editButton = renderer.createComponent(row).children.toList().filterIsInstance<Button>().first()

        editButton.click()
        assertThat(grid.isDetailsVisible(row)).isTrue()

        editButton.click()
        assertThat(grid.isDetailsVisible(row)).isFalse()
    }
}
