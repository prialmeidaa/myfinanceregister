package paa.myfinanceregister.services

import paa.myfinanceregister.domain.model.Statement
import paa.myfinanceregister.domain.model.StatementTransactionType
import paa.myfinanceregister.domain.repository.StatementRepository
import java.time.Year

internal data class SeriesUpdate(
    val seriesId: String,
    val data: StatementFormData,
    val validated: ValidatedStatement
)

internal class StatementSeriesUpdater(private val repository: StatementRepository) {
    fun update(statement: Statement, seriesIndex: Int, update: SeriesUpdate): Statement {
        val schedule = StatementSchedule.create(update.data, update.validated.date, Year.now())
        val futureStatements = repository
            .findBySeriesIdAndSeriesIndexGreaterThanOrderBySeriesIndexAsc(update.seriesId, seriesIndex)
        applyOccurrence(statement, schedule.first(), update)
        val remaining = schedule.drop(1)
        remaining.forEachIndexed { index, occurrence ->
            val future = futureStatements.getOrNull(index) ?: Statement()
            applyOccurrence(future, occurrence, update)
            repository.save(future)
        }
        futureStatements.drop(remaining.size).forEach(repository::delete)
        return repository.save(statement)
    }

    private fun applyOccurrence(
        statement: Statement,
        occurrence: ScheduledStatement,
        update: SeriesUpdate
    ) {
        applyData(statement, update.validated)
        statement.date = occurrence.date
        statement.transactionType = update.data.transactionType
        if (update.data.transactionType == StatementTransactionType.SINGLE) {
            clearSeries(statement)
            return
        }
        statement.seriesId = update.seriesId
        statement.seriesIndex = occurrence.index
        statement.seriesCount = occurrence.count
    }

    private fun applyData(statement: Statement, data: ValidatedStatement) {
        statement.description = data.description
        statement.category = data.category
        statement.account = data.account
        statement.amount = data.amount
    }

    private fun clearSeries(statement: Statement) {
        statement.seriesId = null
        statement.seriesIndex = null
        statement.seriesCount = null
    }
}
