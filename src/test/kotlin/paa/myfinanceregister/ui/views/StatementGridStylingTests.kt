package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.data.renderer.ComponentRenderer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import paa.myfinanceregister.domain.model.Account
import paa.myfinanceregister.domain.model.Category
import paa.myfinanceregister.domain.model.Statement
import java.math.BigDecimal
import java.time.LocalDate

class StatementGridStylingTests {
    @Test
    fun actionsAreFirstColumnAndUseAccessibleIconOnlyButtons() {
        val statement = Statement(
            date = LocalDate.of(2026, 10, 5),
            description = "Lunch",
            category = Category(1, "Food"),
            account = Account(1, "Checking"),
            amount = BigDecimal("10.00")
        )
        val invokedActions = mutableListOf<String>()
        val grid = Grid<StatementGridRow>()

        StatementGridStyling.configure(
            grid,
            { invokedActions.add("edit") },
            { invokedActions.add("delete") }
        )

        val actionsColumn = grid.columns.first()
        val renderer = actionsColumn.renderer as ComponentRenderer<HorizontalLayout, StatementGridRow>
        val layout = renderer.createComponent(StatementGridRow("", "", "", "", BigDecimal.ZERO, statement))
        val buttons = layout.children.toList().filterIsInstance<Button>()

        assertThat(actionsColumn.headerText).isEqualTo("Actions")
        assertThat(actionsColumn.key).isEqualTo("actions")
        assertThat(buttons).hasSize(2)
        assertThat(buttons.map { it.text }).containsExactly("", "")
        assertThat(buttons.map { it.ariaLabel.orElse("") }).containsExactly("Edit", "Delete")
        assertThat(buttons.map { it.tooltip.text }).containsExactly("Edit", "Delete")
        assertThat(buttons.map { (it.icon as Icon).icon })
            .containsExactly(VaadinIcon.PENCIL.create().icon, VaadinIcon.CLOSE.create().icon)
        assertThat(buttons.map { (it.icon as Icon).element.style.get("width") })
            .containsExactly("14px", "14px")
        assertThat(buttons.map { (it.icon as Icon).element.style.get("height") })
            .containsExactly("14px", "14px")

        buttons.forEach(Button::click)
        assertThat(invokedActions).containsExactly("edit", "delete")
    }
}
