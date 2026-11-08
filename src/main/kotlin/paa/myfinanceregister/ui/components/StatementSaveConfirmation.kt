package paa.myfinanceregister.ui.components

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import paa.myfinanceregister.domain.model.Statement
import paa.myfinanceregister.services.StatementFormData

internal object StatementSaveConfirmation {
    fun submit(
        statement: Statement?,
        data: StatementFormData,
        save: (StatementFormData) -> Boolean,
        onSaved: () -> Unit
    ): Dialog? {
        val hasFutureEntries = statement?.seriesId != null &&
            (statement.seriesIndex ?: 1) < (statement.seriesCount ?: 1)
        if (!hasFutureEntries) {
            if (save(data)) onSaved()
            return null
        }
        val dialog = Dialog()
        dialog.add(
            Paragraph("Do you want to update the future statements in this series too?"),
            HorizontalLayout(
                Button("Only this statement") {
                    submitSelection(dialog, data.copy(updateFuture = false), save, onSaved)
                },
                Button("This and future statements") {
                    submitSelection(dialog, data.copy(updateFuture = true), save, onSaved)
                }
            )
        )
        dialog.open()
        return dialog
    }

    private fun submitSelection(
        dialog: Dialog,
        data: StatementFormData,
        save: (StatementFormData) -> Boolean,
        onSaved: () -> Unit
    ) {
        dialog.close()
        if (save(data)) onSaved()
    }
}
