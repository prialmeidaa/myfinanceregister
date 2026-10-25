package paa.myfinanceregister.ui.components

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import paa.myfinanceregister.domain.model.Category

class CategoryFormDialog(
    categories: List<Category>,
    category: Category?,
    private val onSave: (Long?, String, Long?) -> Boolean
) : Dialog() {
    private val rootCategoryId = category?.rootCategory?.id
    private val descriptionField = TextField("Description").apply {
        isRequired = true
        value = category?.description.orEmpty()
    }
    private val rootCategoryField = ComboBox<Category>("Root category").apply {
        setItemLabelGenerator { it.description }
        setItems(categories.filterNot { it.id == category?.id })
        value = categories.firstOrNull { it.id == rootCategoryId }
        setClearButtonVisible(true)
    }

    init {
        headerTitle = if (category == null) "New category" else "Edit category"
        add(
            VerticalLayout(
                descriptionField,
                rootCategoryField,
                HorizontalLayout(
                    Button("Cancel") { close() },
                    Button("Save") { save(category) }
                )
            )
        )
    }

    private fun save(category: Category?) {
        val saved = onSave(category?.id, descriptionField.value, rootCategoryField.value?.id)
        if (saved) close()
    }
}
