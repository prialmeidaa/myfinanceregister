package paa.myfinanceregister.ui.layout

import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.orderedlayout.Scroller
import com.vaadin.flow.server.auth.AnonymousAllowed
import paa.myfinanceregister.ui.components.MainMenu

@AnonymousAllowed
class MainLayout : AppLayout() {
    init {
        addToNavbar(DrawerToggle(), H1("My Finance Register"))
        addToDrawer(H1("My Finance Register"), Scroller(MainMenu()))
        setPrimarySection(Section.DRAWER)
    }
}
