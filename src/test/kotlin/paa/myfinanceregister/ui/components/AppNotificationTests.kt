package paa.myfinanceregister.ui.components

import com.vaadin.flow.component.notification.Notification
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class AppNotificationTests {
    @Test
    fun showUsesTheSharedPositionAndDefaultDuration() {
        Mockito.mockStatic(Notification::class.java).use { notifications ->
            AppNotification.show("Saved")

            notifications.verify {
                Notification.show("Saved", 5000, Notification.Position.TOP_CENTER)
            }
        }
    }

    @Test
    fun showUsesTheRequestedDurationAndSharedPosition() {
        Mockito.mockStatic(Notification::class.java).use { notifications ->
            AppNotification.show("Saved", 3000)

            notifications.verify {
                Notification.show("Saved", 3000, Notification.Position.TOP_CENTER)
            }
        }
    }
}
