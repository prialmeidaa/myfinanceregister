package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll
import paa.myfinanceregister.ui.layout.MainLayout

@Route(value = "accounts", layout = MainLayout::class)
@PermitAll
class AccountsView : VerticalLayout() {
    init {
        add(Paragraph("TODO: Accounts"))
    }
}
