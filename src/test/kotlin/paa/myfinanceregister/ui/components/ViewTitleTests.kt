package paa.myfinanceregister.ui.components

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ViewTitleTests {
    @Test
    fun usesTheSharedViewTitleClassAndText() {
        val title = ViewTitle("Accounts")

        assertThat(title.text).isEqualTo("Accounts")
        assertThat(title.classNames).contains("view-title")
    }
}
