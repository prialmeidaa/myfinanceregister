package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll
import paa.myfinanceregister.domain.model.Account
import paa.myfinanceregister.services.AccountService
import paa.myfinanceregister.ui.components.AppNotification
import paa.myfinanceregister.ui.components.AccountFormDialog
import paa.myfinanceregister.ui.layout.MainLayout

@Route(value = "accounts", layout = MainLayout::class)
@PageTitle("Accounts")
@PermitAll
class AccountsView(private val service: AccountService) : VerticalLayout() {
    private val grid = Grid<Account>()

    init {
        configureGrid()
        add(Button("New account") { openForm() }, grid)
        refreshGrid()
    }

    private fun configureGrid() {
        grid.addColumn(Account::description).setHeader("Description").setAutoWidth(true)
        grid.addColumn { if (it.active) "Active" else "Inactive" }.setHeader("Status")
        grid.addComponentColumn(::createActions).setHeader("Actions")
        grid.setSizeFull()
    }

    private fun createActions(account: Account): Component = HorizontalLayout(
        Button("Edit") { openForm(account) },
        accountStatusButton(account)
    )

    private fun accountStatusButton(account: Account): Button {
        if (account.active) return Button("Deactivate") { confirmDeactivation(account) }
        return Button("Activate") { activateAccount(account) }
    }

    private fun openForm(account: Account? = null) {
        AccountFormDialog(account, ::saveAccount).open()
    }

    private fun saveAccount(id: Long?, description: String): Boolean {
        return try {
            service.save(id, description)
            refreshGrid()
            AppNotification.show("Account saved")
            true
        } catch (exception: IllegalArgumentException) {
            AppNotification.show(exception.message ?: "Invalid account")
            false
        } catch (exception: NoSuchElementException) {
            AppNotification.show(exception.message ?: "Account not found")
            false
        }
    }

    private fun confirmDeactivation(account: Account) {
        val dialog = Dialog()
        dialog.add(
            Paragraph("Deactivate account '${account.description}'?"),
            HorizontalLayout(
                Button("Cancel") { dialog.close() },
                Button("Deactivate") {
                    deactivateAccount(account)
                    dialog.close()
                }
            )
        )
        dialog.open()
    }

    internal fun deactivateAccount(account: Account) {
        try {
            service.deactivate(requireNotNull(account.id))
            refreshGrid()
            AppNotification.show("Account deactivated")
        } catch (exception: NoSuchElementException) {
            AppNotification.show(exception.message ?: "Account not found")
        }
    }

    internal fun activateAccount(account: Account) {
        try {
            service.activate(requireNotNull(account.id))
            refreshGrid()
            AppNotification.show("Account activated")
        } catch (exception: NoSuchElementException) {
            AppNotification.show(exception.message ?: "Account not found")
        }
    }

    private fun refreshGrid() {
        grid.setItems(service.findAll())
    }
}
