package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.notification.Notification
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
    fun deactivationUpdatesAccountAndRefreshesTheGrid() {
        val service = Mockito.mock(AccountService::class.java)
        val account = Account(1, "Checking")
        Mockito.`when`(service.findAll()).thenReturn(listOf(account))
        val view = AccountsView(service)

        Mockito.mockStatic(Notification::class.java).use { notifications ->
            view.deactivateAccount(account)

            Mockito.verify(service).deactivate(1)
            Mockito.verify(service, Mockito.times(2)).findAll()
            notifications.verify { Notification.show("Account deactivated") }
        }
    }
}
