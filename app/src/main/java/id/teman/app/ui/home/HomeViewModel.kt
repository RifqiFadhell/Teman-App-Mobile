package id.teman.app.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.common.convertToRupiah
import id.teman.app.common.orFalse
import id.teman.app.common.orZero
import id.teman.app.domain.model.home.BannerHomeSpec
import id.teman.app.domain.model.home.QuickMenuModel
import id.teman.app.domain.model.user.UserInfo
import id.teman.app.domain.repository.home.HomeRepository
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
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val walletRepository: WalletRepository,
    private val json: Json,
    private val userRepository: UserRepository,
    private val preference: Preference
) : ViewModel() {

    var homeUiState by mutableStateOf(HomeUiState())
        private set

    init {
        getRemoteUserProfile()
        getHomeMenus()
    }

    fun getRemoteUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getUserProfile()
                .catch {
                    /* no-op */
                }
                .collect {
                    val rawJson = json.encodeToString(it)
                    preference.setUserInfo(rawJson)
                    updateProfile()
                }
        }
    }

    fun updateProfile() {
        homeUiState = homeUiState.copy(
            loading = false,
            userName = getUserProfile()?.name.orEmpty(),
            isPinSet = getUserProfile()?.pinStatus,
            imageProfile = getUserProfile()?.userPhoto.orEmpty(),
            point = decimalFormat(getUserProfile()?.point.orZero())
        )
    }

    fun isPinWasSet(): Boolean {
        return getUserProfile()?.pinStatus.orFalse()
    }

    private fun getUserProfile(): UserInfo? {
        var userInfo: UserInfo? = null
        val userInfoJson = runBlocking { preference.getUserInfo.first() }
        if (userInfoJson.isNotBlank()) {
            userInfo = json.decodeFromString(userInfoJson)
        }
        return userInfo
    }

    private fun getHomeMenus() {
        homeUiState = homeUiState.copy(loading = true)
        viewModelScope.launch {
            homeRepository.getHomeMenus().catch { exception ->
                homeUiState =
                    homeUiState.copy(loading = false, error = Event(exception.message.orEmpty()))
                getHomeBanner()
            }.collect {
                getHomeBanner()
                homeUiState = if (it != null) {
                    homeUiState.copy(loading = false, listMenus = it)
                } else {
                    homeUiState.copy(loading = false)
                }
            }
        }
    }

    fun getWalletBalance() {
        homeUiState = homeUiState.copy(loading = true)
        viewModelScope.launch {
            walletRepository.getWalletBalance().catch { exception ->
                homeUiState =
                    homeUiState.copy(loading = false, error = Event(exception.message.orEmpty()))
            }.collect {
                homeUiState = homeUiState.copy(loading = false, balance = it.convertToRupiah())
            }
        }
    }

    private fun getHomeBanner() {
        homeUiState = homeUiState.copy(loading = true)
        viewModelScope.launch {
            homeRepository.getHomeBanners().catch { exception ->
                homeUiState =
                    homeUiState.copy(loading = false, error = Event(exception.message.orEmpty()))
                getWalletBalance()
            }.collect {
                getWalletBalance()
                homeUiState = if (it.isNotEmpty()) {
                    homeUiState.copy(loading = false, listBanners = it)
                } else {
                    homeUiState.copy(loading = false)
                }
            }
        }
    }

    data class HomeUiState(
        val loading: Boolean = false,
        val error: Event<String>? = null,
        val userName: String? = "",
        val isPinSet: Boolean? = false,
        val balance: String = "Rp.0",
        val point: String = "0",
        val imageProfile: String = "",
        val listMenus: List<QuickMenuModel>? = emptyList(),
        val listBanners: List<BannerHomeSpec>? = emptyList()
    )
}