package id.teman.app.ui.myaccount.referral

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.domain.model.referral.ItemReferral
import id.teman.app.domain.model.user.ProfileSpec
import id.teman.app.domain.model.user.UserInfo
import id.teman.app.domain.repository.referral.ReferralRepository
import id.teman.app.domain.repository.user.UserRepository
import id.teman.app.preference.Preference
import id.teman.app.utils.Event
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class ReferralViewModel @Inject constructor(
    private val application: Application,
    private val referralRepository: ReferralRepository,
    private val json: Json,
    private val preference: Preference
) : ViewModel() {

    var uiState by mutableStateOf(ReferralUiState())
        private set

    fun getHistoryReferral() {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            referralRepository.getHistoryReferral().catch { exception ->
                uiState = uiState.copy(
                    isLoading = false,
                    error = Event(exception.message.orEmpty())
                )
            }.collect {
                uiState = uiState.copy(
                    isLoading = false,
                    listReferral = it
                )
            }
        }
    }


    fun getUserProfile(): UserInfo? {
        var userInfo: UserInfo? = null
        val userInfoJson = runBlocking { preference.getUserInfo.first() }
        if (userInfoJson.isNotBlank()) {
            userInfo = json.decodeFromString(userInfoJson)
        }
        return userInfo
    }



    data class ReferralUiState(
        val isLoading: Boolean = false,
        val error: Event<String>? = null,
        val listReferral: List<ItemReferral> = emptyList()
    )
}