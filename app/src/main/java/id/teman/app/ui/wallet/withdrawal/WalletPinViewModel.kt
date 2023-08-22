package id.teman.app.ui.wallet.withdrawal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.domain.model.user.UserInfo
import id.teman.app.domain.model.wallet.withdrawal.WalletDataTransferSpec
import id.teman.app.domain.repository.user.UserRepository
import id.teman.app.domain.repository.wallet.WalletRepository
import id.teman.app.preference.Preference
import id.teman.app.utils.Event
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@HiltViewModel
class WalletPinViewModel @Inject constructor(
    private val json: Json,
    private val walletRepository: WalletRepository,
    private val userRepository: UserRepository,
    private val preference: Preference
): ViewModel() {

    var uiState by mutableStateOf(WalletPinUiState())
        private set

    private fun getUserProfile(): UserInfo? {
        var userInfo: UserInfo? = null
        val userInfoJson = runBlocking { preference.getUserInfo.first() }
        if (userInfoJson.isNotBlank()) {
            userInfo = json.decodeFromString(userInfoJson)
        }
        return userInfo
    }

    fun withdrawBalance(otpCode: String, spec: WalletDataTransferSpec) {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = uiState.copy(isLoading = true)
            walletRepository.withdrawMoney(otpCode, spec)
                .catch { exception ->
                    uiState = uiState.copy(isLoading = false, errorSetupPin = Event(exception.message.orEmpty()))
                }.collect {
                    uiState = uiState.copy(isLoading = false, successWithdraw = Event(Unit))
                }
        }
    }

    fun requestOtpReset() {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            userRepository.sendOtpReset().catch { exception ->
                uiState = uiState.copy(isLoading = false, error = exception.message.orEmpty())
            }.collect {
                uiState = uiState.copy(isLoading = false, successRequestOtp = Event(Unit), number = getUserProfile()?.phoneNumber.orEmpty())
            }
        }
    }
}

data class WalletPinUiState(
    val isLoading: Boolean = false,
    val number: String = "",
    val error: String? = "",
    val successRequestOtp: Event<Unit>? = null,
    val successSetupPin: Event<Unit>? = null,
    val errorSetupPin: Event<String>? = null,
    val successWithdraw: Event<Unit>? = null
)

