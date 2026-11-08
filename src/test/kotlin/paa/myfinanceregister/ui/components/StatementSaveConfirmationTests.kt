package paa.myfinanceregister.ui.components

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import paa.myfinanceregister.domain.model.Statement
import paa.myfinanceregister.domain.model.StatementTransactionType
import paa.myfinanceregister.services.StatementFormData
import java.time.LocalDate

class StatementSaveConfirmationTests {
    @Test
    fun asksWhetherFutureSeriesEntriesShouldBeUpdated() {
        val ui = UI()
        UI.setCurrent(ui)
        try {
            val statement = Statement(
                id = 5, transactionType = StatementTransactionType.INSTALLMENT,
                seriesId = "series", seriesIndex = 1, seriesCount = 3
            )
            var submitted: StatementFormData? = null
            val confirmation = requireNotNull(
                StatementSaveConfirmation.submit(statement, data(), { submitted = it; true }, {})
            )

            assertThat(submitted).isNull()
            descendants(confirmation).filterIsInstance<Button>()
                .single { it.text == "This and future statements" }.click()

            assertThat(submitted?.updateFuture).isTrue()
        } finally {
            UI.setCurrent(null)
        }
    }

    private fun data() = StatementFormData(LocalDate.of(2026, 10, 28), "Purchase", 1, 2, null)

    private fun descendants(component: Component): List<Component> =
        listOf(component) + component.children.toList().flatMap(::descendants)
}
