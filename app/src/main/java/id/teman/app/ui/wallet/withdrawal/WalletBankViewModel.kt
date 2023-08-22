package id.teman.app.ui.wallet.withdrawal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.common.createPartFromString
import id.teman.app.domain.model.wallet.withdrawal.ItemBankSpec
import id.teman.app.domain.model.wallet.withdrawal.WalletBankInformationSpec
import id.teman.app.domain.model.wallet.withdrawal.WalletDataTransferSpec
import id.teman.app.domain.repository.wallet.WalletRepository
import id.teman.app.utils.Event
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import okhttp3.RequestBody

@HiltViewModel
class WalletBankViewModel @Inject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {

    var uiState by mutableStateOf(WalletBankUiState())
        private set

    fun getWalletBankInformation() {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            walletRepository.getWalletBankInformation()
                .catch { exception ->
                    uiState = uiState.copy(isLoading = false)
                }
                .collect {
                    uiState = uiState.copy(isLoading = false, successGetInfo = Event(it))
                }
        }
    }

    fun getListBank() {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            walletRepository.getListBank()
                .catch { exception ->
                    uiState = uiState.copy(isLoading = false)
                }
                .collect {
                    uiState = uiState.copy(isLoading = false, listBank = it)
                }
        }
    }

    fun updateBankInformation(
        accountName: String, accountNumber: String, bankName: String,
        withdrawAmount: Double
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = uiState.copy(isLoading = true)
            val map: MutableMap<String, RequestBody> = mutableMapOf()
            map["name"] = createPartFromString(bankName)
            map["account_name"] = createPartFromString(accountName)
            map["account_number"] = createPartFromString(accountNumber)

            walletRepository.updateWalletBankInformation(map)
                .catch { exception ->
                    uiState = uiState.copy(isLoading = false)
                }
                .collect {
                    uiState = uiState.copy(
                        isLoading = false, successUpdatingBankInformation = Event(
                            WalletDataTransferSpec(
                                it.bankName,
                                it.accountNumber,
                                it.accountName,
                                withdrawAmount
                            )
                        )
                    )
                }
        }
    }

}

data class WalletBankUiState(
    val isLoading: Boolean = false,
    val successGetInfo: Event<WalletBankInformationSpec>? = null,
    val successUpdatingBankInformation: Event<WalletDataTransferSpec>? = null,
    val listBank: List<ItemBankSpec> = emptyList()
)