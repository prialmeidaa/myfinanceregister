package paa.myfinanceregister

import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route

@Route("")
class MainView : VerticalLayout() {
    init {
        add(H1("My Finance Register"))
    }
}
