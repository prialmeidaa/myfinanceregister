package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
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
        val actions = HorizontalLayout(Button("New account") { openForm() }).apply {
            setWidthFull()
            justifyContentMode = FlexComponent.JustifyContentMode.END
        }
        add(actions, grid)
        refreshGrid()
    }

    private fun configureGrid() {
        grid.addComponentColumn(::createActions).setHeader("Actions").setKey("actions")
        grid.addColumn(Account::description).setHeader("Description").setAutoWidth(true)
        grid.addColumn { if (it.active) "Active" else "Inactive" }.setHeader("Status")
        grid.setSizeFull()
    }

    private fun createActions(account: Account): Component = HorizontalLayout(
        Button(VaadinIcon.PENCIL.create().apply { setSize(ACTION_ICON_SIZE) }) { openForm(account) }
            .apply {
                setAriaLabel("Edit")
                setTooltipText("Edit")
            },
        accountStatusButton(account)
    )

    private fun accountStatusButton(account: Account): Button {
        if (account.active) {
            return Button(VaadinIcon.BAN.create().apply { setSize(ACTION_ICON_SIZE) }) {
                confirmDeactivation(account)
            }.apply {
                setAriaLabel("Deactivate")
                setTooltipText("Deactivate")
            }
        }
        return Button(VaadinIcon.CHECK.create().apply { setSize(ACTION_ICON_SIZE) }) { activateAccount(account) }
            .apply {
                setAriaLabel("Activate")
                setTooltipText("Activate")
            }
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

    private companion object {
        const val ACTION_ICON_SIZE = "14px"
    }
}
