package id.teman.app.ui.bill.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.domain.model.bill.CategoryBillSpec
import id.teman.app.domain.repository.bill.BillRepository
import id.teman.app.utils.Event
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class GameViewModel @Inject constructor(
    private val billRepository: BillRepository
) : ViewModel() {

    var gameUiState by mutableStateOf(BillUiState())
        private set

    init {
        viewModelScope.launch {
            gameUiState = gameUiState.copy(loading = true)
            delay(1000)
            getListCategoryGame()
        }
    }

    private fun getListCategoryGame() {
        viewModelScope.launch {
            billRepository.getListCategoryGame().catch { exception ->
                gameUiState = gameUiState.copy(
                    loading = false,
                    error = Event(exception.message.orEmpty())
                )
            }.collect {
                gameUiState = gameUiState.copy(
                    loading = false,
                    listCategoryGame = it
                )
            }
        }
    }
    data class BillUiState(
        val loading: Boolean = false,
        val error: Event<String>? = null,
        val listCategoryGame: List<CategoryBillSpec> = emptyList()
    )
}