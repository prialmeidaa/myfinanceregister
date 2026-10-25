package paa.myfinanceregister.ui.layout

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.Hr
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.orderedlayout.Scroller
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.server.auth.AnonymousAllowed
import paa.myfinanceregister.ui.components.MainMenu

private const val DEFAULT_NAVBAR_TITLE = "My Finance Register"

@AnonymousAllowed
class MainLayout : AppLayout(), BeforeEnterObserver {
    private val pageTitle = H1(DEFAULT_NAVBAR_TITLE)

    init {
        addToNavbar(DrawerToggle(), pageTitle)
        addToDrawer(
            Image("/images/logo-192x192.png", DEFAULT_NAVBAR_TITLE),
            Hr().apply { style.set("margin-inline", "var(--lumo-space-m)") },
            Scroller(MainMenu())
        )
        setPrimarySection(Section.DRAWER)
    }

    override fun beforeEnter(event: BeforeEnterEvent) {
        updateCurrentPageTitle(resolvePageTitle(event.navigationTarget))
    }

    fun updateCurrentPageTitle(title: String) {
        pageTitle.text = title
    }

    private fun resolvePageTitle(target: Class<out Component>): String {
        return target.getAnnotation(PageTitle::class.java)?.value ?: DEFAULT_NAVBAR_TITLE
    }
}
