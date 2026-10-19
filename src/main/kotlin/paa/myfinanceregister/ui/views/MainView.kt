package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

@Route("")
@PermitAll
class MainView : VerticalLayout() {
    init {
        add(H1("My Finance Register"))
    }
}
