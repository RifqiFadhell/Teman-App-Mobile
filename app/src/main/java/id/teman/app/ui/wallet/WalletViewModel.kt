package id.teman.app.ui.wallet

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.common.convertToRupiah
import id.teman.app.domain.model.wallet.WalletItemDetailSpec
import id.teman.app.domain.repository.wallet.WalletRepository
import id.teman.app.utils.Event
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    ) : ViewModel() {

    var walletUiState by mutableStateOf(WalletUiState())
        private set

    fun getWalletBalance() {
        walletUiState = walletUiState.copy(loading = true)
        viewModelScope.launch {
            walletRepository.getWalletBalance().catch { exception ->
                walletUiState =
                    walletUiState.copy(loading = true, error = exception.message.orEmpty())
                getWalletHistory()
            }.collect {
                walletUiState = walletUiState.copy(loading = true, balance = it.convertToRupiah(), balanceDouble = it)
                getWalletHistory()
            }
        }
    }

    private fun getWalletHistory() {
        viewModelScope.launch {
            walletRepository.getWalletHistory().catch { exception ->
                walletUiState =
                    walletUiState.copy(loading = false, error = exception.message.orEmpty())
            }.collect {
                walletUiState = walletUiState.copy(loading = false, historyList = it)
            }
        }
    }

    fun updateStatusBill(transactionId: String) {
        walletUiState = walletUiState.copy(loading = true)
        viewModelScope.launch {
            walletRepository.getDetailTransaction(transactionId).catch { exception ->
                walletUiState =
                    walletUiState.copy(
                        loading = false,
                        error = exception.message.orEmpty()
                    )
            }.collect {
                walletUiState =
                    walletUiState.copy(
                        loading = false, successUpdateBill = Event(it)
                    )
            }
        }
    }

    data class WalletUiState(
        val loading: Boolean = false,
        val error: String? = null,
        val balance: String = "Rp.0",
        val balanceDouble: Double = 0.0,
        val historyList: List<WalletItemDetailSpec>? = emptyList(),
        val successUpdateBill: Event<WalletItemDetailSpec>? = null
    )
}