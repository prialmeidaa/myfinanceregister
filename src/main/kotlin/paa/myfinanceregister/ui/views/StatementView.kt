package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll
import paa.myfinanceregister.ui.layout.MainLayout

@Route(value = "statement", layout = MainLayout::class)
@PageTitle("Statement")
@PermitAll
class StatementView : VerticalLayout() {
    init {
        add(Paragraph("TODO: Statement"))
    }
}
