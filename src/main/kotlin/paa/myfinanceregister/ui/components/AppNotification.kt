package paa.myfinanceregister.ui.components

import com.vaadin.flow.component.notification.Notification

object AppNotification {
    private const val DEFAULT_DURATION = 5000

    fun show(message: String, duration: Int = DEFAULT_DURATION) {
        Notification.show(message, duration, Notification.Position.TOP_CENTER)
    }
}
