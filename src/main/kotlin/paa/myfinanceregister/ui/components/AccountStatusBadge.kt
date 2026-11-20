package paa.myfinanceregister.ui.components

import com.vaadin.flow.component.html.Span

class AccountStatusBadge(active: Boolean) : Span(if (active) "Active" else "Inactive") {
    init {
        addClassName("account-status")
        addClassName(if (active) "account-status-active" else "account-status-inactive")
    }
}
