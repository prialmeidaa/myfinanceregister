package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import paa.myfinanceregister.domain.model.Account
import paa.myfinanceregister.services.AccountService
import paa.myfinanceregister.ui.components.AccountStatusBadge
import paa.myfinanceregister.ui.components.ViewTitle
import paa.myfinanceregister.ui.layout.MainLayout

class AccountsViewTests {
    @Test
    fun accountsViewLoadsAccountsAndUsesSharedLayout() {
        val service = Mockito.mock(AccountService::class.java)
        Mockito.`when`(service.findAll()).thenReturn(listOf(Account(1, "Checking")))
        val view = AccountsView(service)
        val route = AccountsView::class.java.getAnnotation(Route::class.java)
        val title = AccountsView::class.java.getAnnotation(PageTitle::class.java)
        val grid = view.children.toList().filterIsInstance<Grid<Account>>().single()

        assertThat(view.isPadding).isTrue()
        assertThat(view.isSpacing).isTrue()
        assertThat(route.value).isEqualTo("accounts")
        assertThat(route.layout.java).isEqualTo(MainLayout::class.java)
        assertThat(title.value).isEqualTo("Accounts")
        assertThat(AccountsView::class.java.isAnnotationPresent(PermitAll::class.java)).isTrue()
        assertThat(grid.dataProvider.isInMemory).isTrue()
        assertThat(grid.themeNames)
            .contains(GridVariant.NO_BORDER.variantName, GridVariant.ROW_STRIPES.variantName)
        Mockito.verify(service).findAll()
    }

    @Test
    fun newAccountButtonIsAlignedToTheRightOfTheGrid() {
        val view = AccountsView(Mockito.mock(AccountService::class.java))
        val actions = view.children.toList().filterIsInstance<HorizontalLayout>().single()

        assertThat(actions.width).isEqualTo("100%")
        assertThat(actions.justifyContentMode).isEqualTo(FlexComponent.JustifyContentMode.END)
        assertThat(actions.children.toList().filterIsInstance<ViewTitle>().single().text)
            .isEqualTo("Accounts")
        val newAccountButton = actions.children.toList().filterIsInstance<Button>().single()
        assertThat(newAccountButton.text).isEqualTo("New account")
        assertThat(newAccountButton.themeNames).contains(ButtonVariant.PRIMARY.variantName)
    }

    @Test
    fun accountActionsAreFirstAndUseDifferentIconsForStatus() {
        val service = Mockito.mock(AccountService::class.java)
        val grid = AccountsView(service).children.toList()
            .filterIsInstance<Grid<Account>>()
            .single()
        val column = grid.columns.first()
        val renderer = column.renderer as ComponentRenderer<HorizontalLayout, Account>
        val activeButtons = renderer.createComponent(Account(1, "Checking"))
            .children.toList().filterIsInstance<Button>()
        val inactiveButtons = renderer.createComponent(Account(2, "Savings", active = false))
            .children.toList().filterIsInstance<Button>()

        assertThat(column.headerText).isEqualTo("Actions")
        assertThat(column.key).isEqualTo("actions")
        assertThat(activeButtons.map { it.text }).containsExactly("", "")
        assertThat(activeButtons.map { it.ariaLabel.orElse("") }).containsExactly("Edit", "Deactivate")
        assertThat(activeButtons.map { it.tooltip.text }).containsExactly("Edit", "Deactivate")
        assertThat(activeButtons.map { (it.icon as Icon).icon })
            .containsExactly(VaadinIcon.PENCIL.create().icon, VaadinIcon.BAN.create().icon)
        assertThat(activeButtons.map { (it.icon as Icon).element.style.get("width") })
            .containsExactly("16px", "16px")
        assertThat(inactiveButtons.map { it.ariaLabel.orElse("") }).containsExactly("Edit", "Activate")
        assertThat(inactiveButtons.map { it.tooltip.text }).containsExactly("Edit", "Activate")
        assertThat(inactiveButtons.map { (it.icon as Icon).icon })
            .containsExactly(VaadinIcon.PENCIL.create().icon, VaadinIcon.CHECK.create().icon)
    }

    @Test
    fun accountStatusIsRenderedAsActiveOrInactiveBadge() {
        val grid = AccountsView(Mockito.mock(AccountService::class.java)).children
            .toList()
            .filterIsInstance<Grid<Account>>()
            .single()
        val statusColumn = grid.columns.last()
        val renderer = statusColumn.renderer as ComponentRenderer<AccountStatusBadge, Account>

        val activeBadge = renderer.createComponent(Account(1, "Checking"))
        val inactiveBadge = renderer.createComponent(Account(2, "Savings", active = false))

        assertThat(activeBadge.text).isEqualTo("Active")
        assertThat(activeBadge.classNames).contains("account-status", "account-status-active")
        assertThat(inactiveBadge.text).isEqualTo("Inactive")
        assertThat(inactiveBadge.classNames).contains("account-status", "account-status-inactive")
    }

    @Test
    fun deactivationUpdatesAccountAndRefreshesTheGrid() {
        val service = Mockito.mock(AccountService::class.java)
        val account = Account(1, "Checking")
        Mockito.`when`(service.findAll()).thenReturn(listOf(account))
        val view = AccountsView(service)

        Mockito.mockStatic(Notification::class.java).use { notifications ->
            view.deactivateAccount(account)

            Mockito.verify(service).deactivate(1)
            Mockito.verify(service, Mockito.times(2)).findAll()
            notifications.verify {
                Notification.show(
                    "Account deactivated",
                    5000,
                    Notification.Position.TOP_CENTER
                )
            }
        }
    }

    @Test
    fun activationUpdatesAccountAndRefreshesTheGrid() {
        val service = Mockito.mock(AccountService::class.java)
        val account = Account(1, "Checking", active = false)
        Mockito.`when`(service.findAll()).thenReturn(listOf(account))
        val view = AccountsView(service)

        Mockito.mockStatic(Notification::class.java).use { notifications ->
            view.activateAccount(account)

            Mockito.verify(service).activate(1)
            Mockito.verify(service, Mockito.times(2)).findAll()
            notifications.verify {
                Notification.show(
                    "Account activated",
                    5000,
                    Notification.Position.TOP_CENTER
                )
            }
        }
    }
}
