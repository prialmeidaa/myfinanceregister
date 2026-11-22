package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.data.provider.Query
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import paa.myfinanceregister.domain.model.Account
import paa.myfinanceregister.domain.model.Category
import paa.myfinanceregister.domain.model.Statement
import paa.myfinanceregister.domain.model.StatementKind
import paa.myfinanceregister.services.AccountService
import paa.myfinanceregister.services.CategoryService
import paa.myfinanceregister.services.StatementService
import paa.myfinanceregister.ui.components.StatementMonthFilter
import paa.myfinanceregister.ui.components.ViewTitle
import paa.myfinanceregister.ui.layout.MainLayout
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth

class StatementViewTests {
    @Test
    fun newStatementButtonIsAlignedToTheRightOfTheGrid() {
        val view = createView(Mockito.mock(StatementService::class.java))
        val actions = view.children.toList().filterIsInstance<HorizontalLayout>().single()
        val actionComponents = actions.children.toList()

        assertThat(actions.width).isEqualTo("100%")
        assertThat(actionComponents.first()).isInstanceOf(ViewTitle::class.java)
        assertThat((actionComponents.first() as ViewTitle).text).isEqualTo("Statement")
        assertThat(actionComponents[1]).isInstanceOf(StatementMonthFilter::class.java)
        assertThat(actionComponents.last()).isInstanceOf(Button::class.java)
        assertThat((actionComponents.last() as Button).text).isEqualTo("New statement")
        assertThat(actions.getFlexGrow(actionComponents.first())).isEqualTo(1.0)
    }

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
        val cssImports = StatementView::class.java.getAnnotationsByType(CssImport::class.java)
        val grid = view.children.toList().filterIsInstance<Grid<StatementGridRow>>().single()

        assertThat(route.value).isEqualTo("statement")
        assertThat(route.layout.java).isEqualTo(MainLayout::class.java)
        assertThat(title.value).isEqualTo("Statement")
        assertThat(cssImports.map { it.value })
            .containsExactly("./styles/statement-view.css", "./styles/view-title.css")
        assertThat(StatementView::class.java.isAnnotationPresent(PermitAll::class.java)).isTrue()
        assertThat(grid.dataProvider.fetch(Query()).toList().mapNotNull { it.statement })
            .containsExactlyElementsOf(statements.reversed())
    }

    @Test
    fun selectingMultipleMonthsFiltersStatementsToThoseMonths() {
        val statements = listOf(
            statement(LocalDate.of(2026, 10, 5), "October", "30.00"),
            statement(LocalDate.of(2026, 9, 5), "September", "200.00"),
            statement(LocalDate.of(2026, 8, 5), "August", "-5.00")
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
            .containsExactly(statements[2], statements[0])
        assertThat(grid.dataProvider.fetch(Query()).toList()
            .filter { it.totalType == StatementTotalType.DAILY }
            .map { it.amount })
            .containsExactly(BigDecimal("-5.00"), BigDecimal("25.00"))
    }

    @Test
    fun showsDailyBalancesIncludingNegativeStatements() {
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

        val totalRows = rows.filter { it.totalType != null }
        assertThat(totalRows).hasSize(2)
        assertThat(totalRows).allSatisfy {
            assertThat(it.date).isEmpty()
            assertThat(it.description).isEmpty()
            assertThat(it.category).isEmpty()
            assertThat(it.account).isEqualTo("Daily balance")
        }
        val dailyTotals = totalRows.filter { it.totalType == StatementTotalType.DAILY }
        assertThat(dailyTotals.map { it.amount })
            .containsExactly(BigDecimal("75.00"), BigDecimal("85.00"))
    }

    @Test
    fun transferCreatesNegativeOriginAndPositiveDestinationRowsWithNeutralDailyBalance() {
        val transfer = statement(LocalDate.of(2026, 10, 5), "Savings", "50.00").apply {
            kind = StatementKind.TRANSFER
            destinationAccount = Account(2, "Savings")
        }

        val rows = StatementGridRowFactory.create(listOf(transfer))
        val transactionRows = rows.filter { it.totalType == null }
        val dailyBalance = rows.single { it.totalType == StatementTotalType.DAILY }

        assertThat(transactionRows.map { it.category }).containsExactly("Transfer", "Transfer")
        assertThat(transactionRows.map { it.account }).containsExactly("Checking", "Savings")
        assertThat(transactionRows.map { it.amount })
            .containsExactly(BigDecimal("-50.00"), BigDecimal("50.00"))
        assertThat(transactionRows.map { it.statement }).containsExactly(transfer, null)
        assertThat(dailyBalance.amount).isEqualByComparingTo("0.00")
    }

    @Test
    fun editActionOpensAndClosesInlineDetailsForAStatementRow() {
        val statement = statement(LocalDate.of(2026, 10, 5), "Purchase")
        val service = Mockito.mock(StatementService::class.java)
        Mockito.`when`(service.findAll()).thenReturn(listOf(statement))
        val grid = createView(service).children.toList().filterIsInstance<Grid<StatementGridRow>>().single()
        val row = grid.dataProvider.fetch(Query()).toList().first { it.statement != null }
        val renderer = grid.columns.first().renderer as ComponentRenderer<HorizontalLayout, StatementGridRow>
        val editButton = renderer.createComponent(row).children.toList().filterIsInstance<Button>().first()

        editButton.click()
        assertThat(grid.isDetailsVisible(row)).isTrue()

        editButton.click()
        assertThat(grid.isDetailsVisible(row)).isFalse()
    }

    @Test
    fun gridFormatsDatesAndAmountsForBrazilianLocale() {
        val statement = statement(LocalDate.of(2026, 10, 5), "Purchase", "1234.50")
        val rows = StatementGridRowFactory.create(listOf(statement))

        assertThat(rows.first().date).isEqualTo("05/10/2026")
        assertThat(rows[1].date).isEmpty()
        assertThat(rows[1].description).isEmpty()
        assertThat(rows[1].account).isEqualTo("Daily balance")
        assertThat(StatementGridRowFactory.formatAmount(rows.first().amount)).isEqualTo("+1.234,50")
    }

    @Test
    fun assignsGridCellPartsForTotalsAndAmountSigns() {
        val row = StatementGridRow(
            "", "", "", "", BigDecimal("10.00"), totalType = StatementTotalType.DAILY
        )

        assertThat(StatementGridStyling.amountCellParts(row))
            .isEqualTo("statement-total statement-amount-positive")
        assertThat(StatementGridStyling.amountCellParts(row.copy(amount = BigDecimal("-5.00"))))
            .isEqualTo("statement-total statement-amount-negative")
        assertThat(StatementGridStyling.accountCellParts(row))
            .isEqualTo("statement-total statement-total-label")
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
