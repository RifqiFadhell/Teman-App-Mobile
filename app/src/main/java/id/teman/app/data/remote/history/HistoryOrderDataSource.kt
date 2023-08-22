package id.teman.app.data.remote.history

import id.teman.app.data.dto.HistoryOrders
import id.teman.app.data.remote.ApiServiceInterface
import id.teman.app.data.remote.handleRequestOnFlow
import kotlinx.coroutines.flow.Flow

interface HistoryOrderDataSource {
    fun getHistoryOrder(): Flow<HistoryOrders>
}

class DefaultHistoryOrderDataSource(private val httpClient: ApiServiceInterface): HistoryOrderDataSource {

    override fun getHistoryOrder(): Flow<HistoryOrders> =
        handleRequestOnFlow {
            httpClient.getHistoryOrder()
        }
}