package paa.myfinanceregister.services

import paa.myfinanceregister.domain.model.Statement
import paa.myfinanceregister.domain.model.StatementTransactionType
import paa.myfinanceregister.domain.repository.StatementRepository
import java.time.Year
import java.util.UUID

internal class StatementSeriesManager(private val repository: StatementRepository) {
    fun save(
        existing: Statement?,
        data: StatementFormData,
        validated: ValidatedStatement
    ): Statement {
        val statement = existing ?: Statement()
        val seriesId = statement.seriesId
        val seriesIndex = statement.seriesIndex ?: 1
        if (statement.seriesId == null && data.transactionType != StatementTransactionType.SINGLE) {
            applyData(statement, validated)
            return createSeries(statement, data, validated)
        }
        if (seriesId != null && data.updateFuture) {
            return StatementSeriesUpdater(repository).update(
                statement,
                seriesIndex,
                SeriesUpdate(seriesId, data, validated)
            )
        }
        applyData(statement, validated)
        statement.transactionType = data.transactionType
        if (data.transactionType == StatementTransactionType.SINGLE) clearSeries(statement)
        if (seriesId != null) detachFromSeries(statement, data)
        val saved = repository.save(statement)
        return saved
    }

    private fun createSeries(
        statement: Statement,
        data: StatementFormData,
        validated: ValidatedStatement
    ): Statement {
        val schedule = StatementSchedule.create(data, validated.date, Year.now())
        val seriesId = statement.seriesId ?: UUID.randomUUID().toString()
        schedule.forEachIndexed { index, occurrence ->
            val item = if (index == 0) statement else Statement()
            applyData(item, validated)
            item.transactionType = data.transactionType
            item.seriesId = seriesId
            item.seriesIndex = occurrence.index
            item.seriesCount = occurrence.count
            item.date = occurrence.date
            repository.save(item)
        }
        return statement
    }

    private fun applyData(statement: Statement, data: ValidatedStatement) {
        statement.date = data.date
        statement.description = data.description
        statement.category = data.category
        statement.account = data.account
        statement.amount = data.amount
    }

    private fun detachFromSeries(statement: Statement, data: StatementFormData) {
        clearSeries(statement)
        if (data.transactionType == StatementTransactionType.INSTALLMENT) {
            statement.seriesIndex = data.installmentNumber
            statement.seriesCount = data.installmentCount
        }
    }

    private fun clearSeries(statement: Statement) {
        statement.seriesId = null
        statement.seriesIndex = null
        statement.seriesCount = null
    }

}
