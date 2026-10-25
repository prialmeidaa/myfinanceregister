package paa.myfinanceregister.domain.repository

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import paa.myfinanceregister.domain.model.Category

interface CategoryRepository : JpaRepository<Category, Long> {
    @EntityGraph(attributePaths = ["rootCategory"])
    fun findAllByOrderByDescriptionAsc(): List<Category>

    fun existsByRootCategoryId(rootCategoryId: Long): Boolean
}
