package id.teman.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.preference.Preference
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class OnBoardingViewModel @Inject constructor(private val preference: Preference) : ViewModel() {

    fun saveUserHasFinishedOnBoard() {
        viewModelScope.launch {
            preference.setIsOnBoardingFinish(true)
        }
    }
}
