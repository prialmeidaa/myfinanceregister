package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.data.renderer.ComponentRenderer
import paa.myfinanceregister.domain.model.Account
import paa.myfinanceregister.domain.model.Category
import paa.myfinanceregister.services.StatementFormData
import paa.myfinanceregister.ui.components.StatementInlineEditor

internal data class StatementGridDetailsConfiguration(
    val categories: List<Category>,
    val accounts: List<Account>,
    val onSave: (Long?, StatementFormData) -> Boolean
)

internal object StatementGridDetails {
    fun configure(
        grid: Grid<StatementGridRow>,
        configuration: StatementGridDetailsConfiguration
    ): ComponentRenderer<Component, StatementGridRow> {
        grid.setDetailsVisibleOnClick(false)
        val renderer = ComponentRenderer<Component, StatementGridRow> { row ->
            val statement = row.statement
            if (statement == null) {
                Span()
            } else {
                StatementInlineEditor(
                    configuration.categories,
                    configuration.accounts,
                    statement,
                    configuration.onSave
                ) {
                    grid.setDetailsVisible(row, false)
                }
            }
        }
        grid.setItemDetailsRenderer(renderer)
        return renderer
    }
}
