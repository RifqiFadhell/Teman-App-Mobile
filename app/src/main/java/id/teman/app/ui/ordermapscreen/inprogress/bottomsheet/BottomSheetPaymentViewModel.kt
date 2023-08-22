package id.teman.app.ui.ordermapscreen.inprogress.bottomsheet

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.domain.repository.wallet.WalletRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class BottomSheetPaymentViewModel @Inject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {

    var uiState by mutableStateOf(BottomSheetPaymentUiState())
        private set

    fun getWalletBalance() {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            walletRepository.getWalletBalance().catch { exception ->
                uiState = uiState.copy(isLoading = false)
            }.collect {
                uiState = uiState.copy(isLoading = false, totalWalletBalance = it)
            }
        }
    }
}

data class BottomSheetPaymentUiState(
    val isLoading: Boolean = false,
    val totalWalletBalance: Double = 0.0
)