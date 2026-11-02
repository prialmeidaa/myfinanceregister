package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
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

        assertThat(route.value).isEqualTo("accounts")
        assertThat(route.layout.java).isEqualTo(MainLayout::class.java)
        assertThat(title.value).isEqualTo("Accounts")
        assertThat(AccountsView::class.java.isAnnotationPresent(PermitAll::class.java)).isTrue()
        assertThat(grid.dataProvider.isInMemory).isTrue()
        Mockito.verify(service).findAll()
    }

    @Test
    fun newAccountButtonIsAlignedToTheRightOfTheGrid() {
        val view = AccountsView(Mockito.mock(AccountService::class.java))
        val actions = view.children.toList().filterIsInstance<HorizontalLayout>().single()

        assertThat(actions.width).isEqualTo("100%")
        assertThat(actions.justifyContentMode).isEqualTo(FlexComponent.JustifyContentMode.END)
        assertThat(actions.children.toList().filterIsInstance<Button>().single().text)
            .isEqualTo("New account")
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
            .containsExactly("14px", "14px")
        assertThat(inactiveButtons.map { it.ariaLabel.orElse("") }).containsExactly("Edit", "Activate")
        assertThat(inactiveButtons.map { it.tooltip.text }).containsExactly("Edit", "Activate")
        assertThat(inactiveButtons.map { (it.icon as Icon).icon })
            .containsExactly(VaadinIcon.PENCIL.create().icon, VaadinIcon.CHECK.create().icon)
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
