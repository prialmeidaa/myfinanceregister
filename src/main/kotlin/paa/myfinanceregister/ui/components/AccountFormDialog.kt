package paa.myfinanceregister.ui.components

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import paa.myfinanceregister.domain.model.Account

class AccountFormDialog(
    account: Account?,
    private val onSave: (Long?, String) -> Boolean
) : Dialog() {
    private val descriptionField = TextField("Description").apply {
        isRequired = true
        setMaxLength(Account.MAX_DESCRIPTION_LENGTH)
        setMaxWidth(DESCRIPTION_FIELD_MAX_WIDTH)
        value = account?.description.orEmpty()
    }

    init {
        headerTitle = if (account == null) "New account" else "Edit account"
        add(
            VerticalLayout(
                descriptionField,
                HorizontalLayout(
                    Button("Cancel") { close() },
                    Button("Save") { save(account) }
                )
            )
        )
    }

    private fun save(account: Account?) {
        val saved = onSave(account?.id, descriptionField.value)
        if (saved) close()
    }

    private companion object {
        const val DESCRIPTION_FIELD_MAX_WIDTH = "24rem"
    }
}
