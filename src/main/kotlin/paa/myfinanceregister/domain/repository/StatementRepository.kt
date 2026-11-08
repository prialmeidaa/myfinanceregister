package paa.myfinanceregister.domain.repository

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import paa.myfinanceregister.domain.model.Statement

interface StatementRepository : JpaRepository<Statement, Long> {
    @EntityGraph(attributePaths = ["category", "account", "destinationAccount"])
    fun findAllByOrderByDateDesc(): List<Statement>

    fun existsByCategoryId(categoryId: Long): Boolean

    fun findBySeriesIdAndSeriesIndexGreaterThanOrderBySeriesIndexAsc(
        seriesId: String,
        seriesIndex: Int
    ): List<Statement>
}
