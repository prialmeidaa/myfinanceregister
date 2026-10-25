package paa.myfinanceregister.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import paa.myfinanceregister.domain.model.Category
import paa.myfinanceregister.domain.repository.CategoryRepository

@Service
class CategoryService(private val repository: CategoryRepository) {
    @Transactional(readOnly = true)
    fun findAll(): List<Category> = repository.findAllByOrderByDescriptionAsc()

    @Transactional
    fun save(id: Long?, description: String, rootCategoryId: Long?): Category {
        val normalizedDescription = description.trim()
        require(normalizedDescription.isNotEmpty()) { "Description is required" }

        val category = id?.let(::loadCategory) ?: Category()
        val rootCategory = rootCategoryId?.let(::loadCategory)
        validateRootCategory(category, rootCategory)

        category.description = normalizedDescription
        category.rootCategory = rootCategory
        return repository.save(category)
    }

    @Transactional
    fun delete(id: Long) {
        val category = loadCategory(id)
        check(!repository.existsByRootCategoryId(id)) {
            "Cannot delete a category that has subcategories"
        }
        repository.delete(category)
    }

    private fun loadCategory(id: Long): Category =
        repository.findById(id).orElseThrow { NoSuchElementException("Category $id not found") }

    private fun validateRootCategory(category: Category, rootCategory: Category?) {
        var ancestor = rootCategory
        val visited = mutableSetOf<Long>()

        while (ancestor != null) {
            require(ancestor.id != category.id) { "A category cannot be its own root category" }
            val ancestorId = ancestor.id
            if (ancestorId != null) {
                check(visited.add(ancestorId)) { "The category hierarchy contains a cycle" }
            }
            ancestor = ancestor.rootCategory
        }
    }
}
