package paa.myfinanceregister.ui.components

import com.vaadin.flow.component.html.H1

class ViewTitle(title: String) : H1(title) {
    init {
        addClassName("view-title")
    }
}
