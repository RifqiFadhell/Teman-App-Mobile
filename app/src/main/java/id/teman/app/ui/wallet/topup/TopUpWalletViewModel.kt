package id.teman.app.ui.wallet.topup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.domain.repository.wallet.WalletRepository
import id.teman.app.utils.Event
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopUpWalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {

    var topUpWalletUiState by mutableStateOf(TopUpWalletUiState())
        private set

    fun requestTopUpWallet(amount: Int) {
        topUpWalletUiState = topUpWalletUiState.copy(loading = true)
        viewModelScope.launch {
            walletRepository.topUpBalanceWallet(amount).catch { exception ->
                topUpWalletUiState = topUpWalletUiState.copy(
                    loading = false,
                    error = Event(exception.message.orEmpty())
                )
            }.collect {
                topUpWalletUiState =
                    topUpWalletUiState.copy(loading = false, successRequestTopUp = Event(it.url))
            }
        }
    }

    data class TopUpWalletUiState(
        val loading: Boolean = false,
        val error: Event<String>? = null,
        val successRequestTopUp: Event<String>? = null
    )
}