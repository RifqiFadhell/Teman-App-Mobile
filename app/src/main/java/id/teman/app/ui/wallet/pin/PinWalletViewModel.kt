package id.teman.app.ui.wallet.pin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.data.dto.bill.BuyBillRequestDto
import id.teman.app.data.dto.bill.BuyPulsaRequestDto
import id.teman.app.data.dto.wallet.pin.PinWalletRequestDto
import id.teman.app.domain.model.bill.SuccessBillSpec
import id.teman.app.domain.model.user.UserInfo
import id.teman.app.domain.model.wallet.WalletItemDetailSpec
import id.teman.app.domain.repository.bill.BillRepository
import id.teman.app.domain.repository.user.UserRepository
import id.teman.app.domain.repository.wallet.PinWalletRepository
import id.teman.app.domain.repository.wallet.WalletRepository
import id.teman.app.preference.Preference
import id.teman.app.utils.Event
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@HiltViewModel
class PinWalletViewModel @Inject constructor(
    private val pinWalletRepository: PinWalletRepository,
    private val json: Json,
    private val billRepository: BillRepository,
    private val walletRepository: WalletRepository,
    private val userRepository: UserRepository,
    private val preference: Preference
) : ViewModel() {

    var pinWalletUiState by mutableStateOf(PinWalletUiState())
        private set

    private fun getUserProfile(): UserInfo? {
        var userInfo: UserInfo? = null
        val userInfoJson = runBlocking { preference.getUserInfo.first() }
        if (userInfoJson.isNotBlank()) {
            userInfo = json.decodeFromString(userInfoJson)
        }
        return userInfo
    }

    fun updatePinWallet(pin: String, token: String? = "") {
        pinWalletUiState = pinWalletUiState.copy(loading = true)
        viewModelScope.launch {
            pinWalletRepository.updatePinWallet(PinWalletRequestDto(pin, token))
                .catch { exception ->
                    pinWalletUiState =
                        pinWalletUiState.copy(
                            loading = false,
                            error = exception.cause?.message
                        )
                }.collect {
                    pinWalletUiState =
                        pinWalletUiState.copy(loading = false, successSetPin = Event(Unit))
                    updateUserInfo()
                }
        }
    }

    fun buyPulsaValidation(number: String, code: String, pin: String) {
        pinWalletUiState = pinWalletUiState.copy(loading = true)
        viewModelScope.launch {
            billRepository.buyPulsa(BuyPulsaRequestDto(number, code, pin)).catch { exception ->
                pinWalletUiState =
                    pinWalletUiState.copy(
                        loading = false,
                        error = exception.cause?.message
                    )
            }.collect {
                pinWalletUiState =
                    pinWalletUiState.copy(
                        loading = false, successBuyBill = Event(it)
                    )
            }
        }
    }

    fun buyBillValidation(number: String, code: String, pin: String, category: String) {
        pinWalletUiState = pinWalletUiState.copy(loading = true)
        viewModelScope.launch {
            billRepository.buyBill(BuyBillRequestDto(number, category, code, pin))
                .catch { exception ->
                    pinWalletUiState =
                        pinWalletUiState.copy(
                            loading = false,
                            error = exception.message.orEmpty()
                        )
                }.collect {
                pinWalletUiState =
                    pinWalletUiState.copy(
                        loading = false, successBuyBill = Event(it)
                    )
            }
        }
    }

    fun updateStatusBill(transactionId: String) {
        pinWalletUiState = pinWalletUiState.copy(loading = true)
        viewModelScope.launch {
            walletRepository.getDetailTransaction(transactionId).catch { exception ->
                pinWalletUiState =
                    pinWalletUiState.copy(
                        loading = false,
                        error = exception.message.orEmpty()
                    )
            }.collect {
                pinWalletUiState =
                    pinWalletUiState.copy(
                        loading = false, successUpdateBill = Event(it)
                    )
            }
        }
    }

    private fun updateUserInfo() {
        viewModelScope.launch {
            val userInfo = getUserProfile()
            if (userInfo != null) {
                val newUserInfo = userInfo.copy(pinStatus = true)
                preference.setUserInfo(json.encodeToString(newUserInfo))
            }
        }
    }

    fun requestOtpReset() {
        pinWalletUiState = pinWalletUiState.copy(loading = true)
        viewModelScope.launch {
            userRepository.sendOtpReset().catch { exception ->
                pinWalletUiState = pinWalletUiState.copy(loading = false, error = exception.message.orEmpty())
            }.collect {
                pinWalletUiState = pinWalletUiState.copy(loading = false, successRequestOtp = Event(Unit), number = getUserProfile()?.phoneNumber.orEmpty())
            }
        }
    }


    data class PinWalletUiState(
        val loading: Boolean = false,
        val error: String? = "",
        val number: String = "",
        val successSetPin: Event<Unit>? = null,
        val successRequestOtp: Event<Unit>? = null,
        val successBuyBill: Event<SuccessBillSpec>? = null,
        val successUpdateBill: Event<WalletItemDetailSpec>? = null
    )
}