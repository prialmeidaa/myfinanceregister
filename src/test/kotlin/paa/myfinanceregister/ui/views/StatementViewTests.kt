package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.data.provider.Query
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import paa.myfinanceregister.domain.model.Account
import paa.myfinanceregister.domain.model.Category
import paa.myfinanceregister.domain.model.Statement
import paa.myfinanceregister.services.AccountService
import paa.myfinanceregister.services.CategoryService
import paa.myfinanceregister.services.StatementService
import paa.myfinanceregister.ui.components.StatementMonthFilter
import paa.myfinanceregister.ui.layout.MainLayout
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth

class StatementViewTests {
    @Test
    fun statementViewUsesSharedLayoutAndShowsAllStatementsInitially() {
        val statements = listOf(
            statement(LocalDate.of(2026, 10, 5), "October"),
            statement(LocalDate.of(2026, 9, 5), "September")
        )
        val service = Mockito.mock(StatementService::class.java)
        Mockito.`when`(service.findAll()).thenReturn(statements)
        val view = createView(service)
        val route = StatementView::class.java.getAnnotation(Route::class.java)
        val title = StatementView::class.java.getAnnotation(PageTitle::class.java)
        val grid = view.children.toList().filterIsInstance<Grid<StatementGridRow>>().single()

        assertThat(route.value).isEqualTo("statement")
        assertThat(route.layout.java).isEqualTo(MainLayout::class.java)
        assertThat(title.value).isEqualTo("Statement")
        assertThat(StatementView::class.java.isAnnotationPresent(PermitAll::class.java)).isTrue()
        assertThat(grid.dataProvider.fetch(Query()).toList().mapNotNull { it.statement })
            .containsExactlyElementsOf(statements)
    }

    @Test
    fun selectingMultipleMonthsFiltersStatementsToThoseMonths() {
        val statements = listOf(
            statement(LocalDate.of(2026, 10, 5), "October"),
            statement(LocalDate.of(2026, 9, 5), "September"),
            statement(LocalDate.of(2026, 8, 5), "August")
        )
        val service = Mockito.mock(StatementService::class.java)
        Mockito.`when`(service.findAll()).thenReturn(statements)
        val view = createView(service)
        val monthFilter = view.children.toList()
            .flatMap { it.children.toList() }
            .filterIsInstance<StatementMonthFilter>()
            .single()
        monthFilter.value = setOf(YearMonth.of(2026, 10), YearMonth.of(2026, 8))
        val grid = view.children.toList().filterIsInstance<Grid<StatementGridRow>>().single()

        assertThat(grid.dataProvider.fetch(Query()).toList().mapNotNull { it.statement })
            .containsExactly(statements[0], statements[2])
    }

    @Test
    fun showsDailyAndMonthlyTotalsIncludingNegativeStatements() {
        val statements = listOf(
            statement(LocalDate.of(2026, 10, 5), "Purchase", "100.00"),
            statement(LocalDate.of(2026, 10, 5), "Refund", "-25.00"),
            statement(LocalDate.of(2026, 10, 8), "Another purchase", "10.00")
        )
        val service = Mockito.mock(StatementService::class.java)
        Mockito.`when`(service.findAll()).thenReturn(statements)
        val grid = createView(service).children.toList()
            .filterIsInstance<Grid<StatementGridRow>>()
            .single()
        val rows = grid.dataProvider.fetch(Query()).toList()

        assertThat(rows.filter { it.description == "Daily total" }.map { it.amount })
            .containsExactly(BigDecimal("10.00"), BigDecimal("75.00"))
        assertThat(rows.single { it.description == "Monthly total" }.amount)
            .isEqualByComparingTo("85.00")
    }

    @Test
    fun gridFormatsDatesAndAmountsForBrazilianLocale() {
        val statement = statement(LocalDate.of(2026, 10, 5), "Purchase", "1234.50")
        val rows = StatementGridRowFactory.create(listOf(statement))

        assertThat(rows.first().date).isEqualTo("05/10/2026")
        assertThat(rows.first { it.description == "Daily total" }.date).isEqualTo("05/10/2026")
        assertThat(StatementGridRowFactory.formatAmount(rows.first().amount)).isEqualTo("1.234,50")
    }

    @Test
    fun deleteStatementRemovesItAndRefreshesTheGrid() {
        val statement = statement(LocalDate.of(2026, 10, 5), "Lunch").apply { id = 1 }
        val service = Mockito.mock(StatementService::class.java)
        Mockito.`when`(service.findAll()).thenReturn(listOf(statement), emptyList())
        val view = createView(service)
        val grid = view.children.toList().filterIsInstance<Grid<StatementGridRow>>().single()

        Mockito.mockStatic(Notification::class.java).use { notifications ->
            view.deleteStatement(statement)

            Mockito.verify(service).delete(1)
            assertThat(grid.dataProvider.fetch(Query()).toList()).isEmpty()
            notifications.verify {
                Notification.show(
                    "Statement deleted",
                    5000,
                    Notification.Position.TOP_CENTER
                )
            }
        }
    }

    private fun createView(service: StatementService) = StatementView(
        service,
        Mockito.mock(CategoryService::class.java),
        Mockito.mock(AccountService::class.java)
    )

    private fun statement(date: LocalDate, description: String, amount: String = "0.00") = Statement(
        date = date,
        description = description,
        category = Category(1, "Food"),
        account = Account(1, "Checking"),
        amount = BigDecimal(amount)
    )
}
