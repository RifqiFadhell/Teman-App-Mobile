package id.teman.app.ui.bill

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.domain.model.bill.CategoryBillSpec
import id.teman.app.domain.repository.bill.BillRepository
import id.teman.app.utils.Event
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class BillViewModel @Inject constructor(
    private val billRepository: BillRepository
) : ViewModel() {

    var billUiState by mutableStateOf(BillUiState())
        private set

    fun getListCategoryBill() {
        viewModelScope.launch {
            billRepository.getListCategoryBill().catch { exception ->
                billUiState = billUiState.copy(
                    loading = false,
                    error = Event(exception.message.orEmpty())
                )
            }.collect {
                billUiState = billUiState.copy(
                    loading = false,
                    listCategoryBill = it
                )
            }
        }
    }

    data class BillUiState(
        val loading: Boolean = false,
        val error: Event<String>? = null,
        val listCategoryBill: List<CategoryBillSpec> = emptyList()
    )
}