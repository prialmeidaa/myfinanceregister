package paa.myfinanceregister.ui.components

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.textfield.TextField
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import paa.myfinanceregister.domain.model.Account

class AccountFormDialogTests {
    @Test
    fun editDialogPrepopulatesDescriptionAndSubmitsTheExistingId() {
        val account = Account(4, "Checking")
        var submittedId: Long? = null
        var submittedDescription = ""
        val dialog = AccountFormDialog(account) { id, description ->
            submittedId = id
            submittedDescription = description
            false
        }
        val components = descendants(dialog)

        val descriptionField = components.filterIsInstance<TextField>().single()
        assertThat(descriptionField.value).isEqualTo("Checking")
        assertThat(descriptionField.maxLength).isEqualTo(Account.MAX_DESCRIPTION_LENGTH)
        assertThat(descriptionField.maxWidth).isEqualTo("24rem")
        components.filterIsInstance<Button>().single { it.text == "Save" }.click()

        assertThat(submittedId).isEqualTo(4)
        assertThat(submittedDescription).isEqualTo("Checking")
    }

    private fun descendants(component: Component): List<Component> =
        listOf(component) + component.children.toList().flatMap(::descendants)
}
