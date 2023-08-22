package id.teman.app.ui.myaccount.reward

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.common.orZero
import id.teman.app.domain.model.reward.ItemReward
import id.teman.app.domain.model.reward.ItemRewardRedeemed
import id.teman.app.domain.model.reward.ItemRewardTransaction
import id.teman.app.domain.model.user.UserInfo
import id.teman.app.domain.repository.user.UserRepository
import id.teman.app.domain.repository.wallet.WalletRepository
import id.teman.app.preference.Preference
import id.teman.app.utils.Event
import id.teman.app.utils.decimalFormat
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@HiltViewModel
class RewardViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val json: Json,
    private val userRepository: UserRepository,
    private val preference: Preference
) : ViewModel() {

    var uiState by mutableStateOf(WalletUiState())
        private set

    fun getUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getUserProfile()
                .catch {
                    /* no-op */
                }
                .collect {
                    val rawJson = json.encodeToString(it)
                    preference.setUserInfo(rawJson)
                }
        }
    }

    private fun getUserInfo(): UserInfo? {
        val userRawJson = runBlocking { preference.getUserInfo.first() }
        return if (userRawJson.isBlank()) null else json.decodeFromString(userRawJson)
    }

    fun getHistoryPoint() {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            walletRepository.getHistoryPoint().catch { exception ->
                uiState = uiState.copy(
                    isLoading = false,
                    error = exception.message.orEmpty()
                )
            }.collect {
                uiState = uiState.copy(
                    isLoading = false,
                    historyPoint = it
                )
            }
        }
    }

    fun initPageReward() {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            walletRepository.getRewardRedeemed().catch { exception ->
                uiState = uiState.copy(
                    isLoading = false,
                    error = exception.message.orEmpty(),
                    rewardPoint = decimalFormat(getUserInfo()?.point.orZero())
                )
            }.collect {
                uiState = uiState.copy(
                    isLoading = false,
                    transactions = it,
                    rewardPoint = decimalFormat(getUserInfo()?.point.orZero())
                )
            }
        }
    }

    fun initPageListReward() {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            walletRepository.getRewards().catch { exception ->
                uiState = uiState.copy(
                    isLoading = false,
                    error = exception.message.orEmpty(),
                    rewardPoint = decimalFormat(getUserInfo()?.point.orZero())
                )
            }.collect {
                uiState = uiState.copy(
                    isLoading = false,
                    rewards = it,
                    rewardPoint = decimalFormat(getUserInfo()?.point.orZero())
                )
            }
        }
    }

    fun redeemReward(id: String) {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            walletRepository.redeemReward(id).catch { exception ->
                uiState = uiState.copy(isLoading = false, error = exception.message.orEmpty())
            }.collect {
                uiState = uiState.copy(isLoading = false, successRedeem = Event(Unit))
            }
        }
    }
}

data class WalletUiState(
    val isLoading: Boolean = false,
    val error: String = "",
    val successRedeem: Event<Unit>? = null,
    val rewardPoint: String = "",
    val transactions: List<ItemRewardRedeemed> = emptyList(),
    val historyPoint: List<ItemRewardTransaction> = emptyList(),
    val rewards: List<ItemReward> = emptyList(),
)