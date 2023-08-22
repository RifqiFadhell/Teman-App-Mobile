package id.teman.app.ui.bill.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.data.dto.bill.ListBillRequestDto
import id.teman.app.data.dto.bill.ListPulsaRequestDto
import id.teman.app.domain.model.bill.BillSpec
import id.teman.app.domain.repository.bill.BillRepository
import id.teman.app.utils.Event
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class ProviderViewModel @Inject constructor(
    private val billRepository: BillRepository
) : ViewModel() {

    var providerUiState by mutableStateOf(BillUiState())
        private set

    private var searchJob: Job? = null


    fun searchDebounced(number: String, key: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(1000)
            if (key == "pulsa") getListPulsa(number) else getListBill(category = key, number)
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (searchJob != null) {
            searchJob?.cancel()
            providerUiState = providerUiState.copy(
                loading = false,
                listBillPrice = emptyList()
            )
        }
    }

    private fun getListPulsa(number: String) {
        providerUiState = providerUiState.copy(loading = true)
        viewModelScope.launch {
            billRepository.getListPricePulsa(ListPulsaRequestDto(number)).catch { exception ->
                providerUiState = providerUiState.copy(
                    loading = false,
                    error = Event(exception.message.orEmpty())
                )
            }.collect {
                providerUiState = providerUiState.copy(
                    loading = false,
                    listBillPrice = it
                )
            }
        }
    }

    fun getListBill(category: String, number: String) {
        providerUiState = providerUiState.copy(loading = true)
        viewModelScope.launch {
            billRepository.getListBillPrice(ListBillRequestDto(customer_no = number, category =  category))
                .catch { exception ->
                    providerUiState = providerUiState.copy(
                        loading = false,
                        error = Event(exception.message.orEmpty())
                    )
                }.collect {
                providerUiState = providerUiState.copy(
                    loading = false,
                    listBillPrice = it
                )
            }
        }
    }

    fun resetListData() {
        providerUiState = providerUiState.copy(
            loading = false,
            listBillPrice = emptyList()
        )
    }

    data class BillUiState(
        val loading: Boolean = false,
        val error: Event<String>? = null,
        val listBillPrice: List<BillSpec> = emptyList(),
        val successBuyPulsa: Event<String>? = null,
        val successBuyBill: Event<String>? = null
    )
}