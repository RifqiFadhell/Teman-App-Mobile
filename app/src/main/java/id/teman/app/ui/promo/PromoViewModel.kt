package id.teman.app.ui.promo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.domain.model.promo.PromoUiModel
import id.teman.app.domain.repository.promo.PromoRepository
import id.teman.app.ui.ordermapscreen.initiate.send.PromoSpec
import id.teman.app.utils.Event
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class PromoViewModel @Inject constructor(
    private val promoRepository: PromoRepository
) : ViewModel() {

    var promoUiState by mutableStateOf(PromoUiState())
        private set

    fun getPromoList() {
        promoUiState = promoUiState.copy(loading = true)
        viewModelScope.launch {
            promoRepository.getAllPromo().catch { exception ->
                promoUiState = promoUiState.copy(loading = false, error = exception.message.orEmpty())
            }.collect {
                promoUiState = promoUiState.copy(loading = false, promoList = it)
            }
        }
    }

    fun setPromo(spec: PromoSpec) {
        promoUiState = promoUiState.copy(setPromo = Event(spec))
    }

    data class PromoUiState(
        val loading: Boolean = false,
        val error: String? = null,
        val promoList: List<PromoUiModel>? = emptyList(),
        val setPromo: Event<PromoSpec>? = null
    )
}