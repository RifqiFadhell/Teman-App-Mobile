package id.teman.app.domain.repository.history

import id.teman.app.data.remote.history.HistoryOrderDataSource
import id.teman.app.domain.model.history.HistoryModel
import id.teman.app.domain.model.history.toHistoryModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HistoryRepository @Inject constructor(
    private val historyOrderDataSource: HistoryOrderDataSource
) {
    suspend fun getHistoryOrder(): Flow<List<HistoryModel>> =
        historyOrderDataSource.getHistoryOrder().map { it.data.toHistoryModel() }
}