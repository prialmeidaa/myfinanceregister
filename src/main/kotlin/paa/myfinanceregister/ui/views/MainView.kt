package paa.myfinanceregister.ui.views

import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.Nav
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

@Route("")
@PermitAll
class MainView : VerticalLayout() {
    init {
        val navigation = Nav().apply {
            add(
                HorizontalLayout(
                    Anchor("/", "Home"),
                    Anchor("/accounts", "Accounts"),
                    Anchor("/statement", "Statement")
                )
            )
        }

        add(navigation, H1("My Finance Register"))
    }
}
