package paa.myfinanceregister.ui.components

import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.sidenav.SideNav
import com.vaadin.flow.component.sidenav.SideNavItem

class MainMenu : SideNav() {
    init {
        addItem(
            SideNavItem("Home", "/", VaadinIcon.HOME.create()),
            SideNavItem("Accounts", "/accounts", VaadinIcon.BUILDING.create()),
            SideNavItem("Categories", "/categories", VaadinIcon.TAGS.create()),
            SideNavItem("Statement", "/statement", VaadinIcon.LIST.create())
        )
    }
}
