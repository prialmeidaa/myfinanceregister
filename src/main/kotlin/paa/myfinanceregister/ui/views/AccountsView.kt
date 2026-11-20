package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.confirmdialog.ConfirmDialog
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
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
import paa.myfinanceregister.ui.components.AccountStatusBadge
import paa.myfinanceregister.ui.components.ViewTitle
import paa.myfinanceregister.ui.layout.MainLayout

@Route(value = "accounts", layout = MainLayout::class)
@PageTitle("Accounts")
@PermitAll
@CssImport("./styles/accounts-view.css")
@CssImport("./styles/view-title.css")
class AccountsView(private val service: AccountService) : VerticalLayout() {
    private val grid = Grid<Account>()

    init {
        addClassName("accounts-view")
        configureGrid()
        val title = ViewTitle("Accounts")
        val header = HorizontalLayout(
            title,
            Button("New account") { openForm() }.apply {
                addThemeVariants(ButtonVariant.PRIMARY)
            }
        ).apply {
            setWidthFull()
            justifyContentMode = FlexComponent.JustifyContentMode.END
            setFlexGrow(1.0, title)
            addClassName("view-header")
        }
        add(header, grid)
        refreshGrid()
    }

    private fun configureGrid() {
        grid.addThemeVariants(GridVariant.NO_BORDER, GridVariant.ROW_STRIPES)
        grid.addComponentColumn(::createActions).setHeader("Actions").setKey("actions")
        grid.addColumn(Account::description).setHeader("Description").setAutoWidth(true)
        grid.addComponentColumn { AccountStatusBadge(it.active) }.setHeader("Status").setAutoWidth(true)
        grid.setSizeFull()
        grid.setEmptyStateText("No accounts have been created")
    }

    private fun createActions(account: Account): Component = HorizontalLayout(
        Button(VaadinIcon.PENCIL.create().apply { setSize(ACTION_ICON_SIZE) }) { openForm(account) }
            .apply {
                setAriaLabel("Edit")
                setTooltipText("Edit")
                addThemeVariants(ButtonVariant.TERTIARY)
            },
        accountStatusButton(account)
    ).apply {
        setSpacing(false)
        addClassName("account-actions")
    }

    private fun accountStatusButton(account: Account): Button {
        if (account.active) {
            return Button(VaadinIcon.BAN.create().apply { setSize(ACTION_ICON_SIZE) }) {
                confirmDeactivation(account)
            }.apply {
                setAriaLabel("Deactivate")
                setTooltipText("Deactivate")
                addThemeVariants(ButtonVariant.ERROR, ButtonVariant.TERTIARY)
            }
        }
        return Button(VaadinIcon.CHECK.create().apply { setSize(ACTION_ICON_SIZE) }) { activateAccount(account) }
            .apply {
                setAriaLabel("Activate")
                setTooltipText("Activate")
                addThemeVariants(ButtonVariant.SUCCESS, ButtonVariant.TERTIARY)
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
        val dialog = ConfirmDialog().apply {
            setHeader("Deactivate account?")
            setText("The account '${account.description}' will become inactive.")
            setCancelable(true)
            setCancelText("Cancel")
            setConfirmText("Deactivate")
            setConfirmButtonTheme("error primary")
            addConfirmListener { deactivateAccount(account) }
        }
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
        const val ACTION_ICON_SIZE = "16px"
    }
}
