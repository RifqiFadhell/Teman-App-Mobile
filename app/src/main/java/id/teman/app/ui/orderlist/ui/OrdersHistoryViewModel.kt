package id.teman.app.ui.orderlist.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.domain.model.history.HistoryModel
import id.teman.app.domain.model.order.TransportRequestType
import id.teman.app.domain.repository.history.HistoryRepository
import id.teman.app.domain.repository.order.OrderRepository
import id.teman.app.utils.Event
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@HiltViewModel
class OrdersHistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val orderRepository: OrderRepository
): ViewModel() {

    var historyUiState by mutableStateOf(HistoryUiState())
    private set

    fun getHistoryList() {
        historyUiState = historyUiState.copy(loading = true)
        viewModelScope.launch {
            historyRepository.getHistoryOrder().catch { exception ->
                historyUiState =
                    historyUiState.copy(loading = false, error = exception.message.orEmpty())
            }.collect {
                historyUiState = historyUiState.copy(loading = false, historyList = it)
            }
        }
    }

    fun sendRating(rating: Int, feedback: String, orderId: String) {
        historyUiState = historyUiState.copy(loading = true)
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.sendRating(orderId, feedback, rating)
                .catch {
                    historyUiState = historyUiState.copy(loading = false)
                }.collect {
                    historyUiState =
                        historyUiState.copy(loading = false, successRating = Event(Unit))
                    orderRepository.updateOrderStatus(orderId, TransportRequestType.FINISHED)
                        .catch { }
                        .collect()
                }
        }
    }

    data class HistoryUiState(
        val loading: Boolean = false,
        val error: String? = null,
        val successRating: Event<Unit>? = null,
        val historyList: List<HistoryModel>? = emptyList()
    )
}