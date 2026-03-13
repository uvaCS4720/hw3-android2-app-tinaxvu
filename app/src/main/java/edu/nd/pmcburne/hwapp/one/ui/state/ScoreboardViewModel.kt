package edu.nd.pmcburne.hwapp.one.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.nd.pmcburne.hwapp.one.BasketballRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import edu.nd.pmcburne.hwapp.one.local.GameEntity
import kotlinx.coroutines.Job

data class ScoreboardUiState(
    val games: List<GameEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val isWomens: Boolean = false
)

class ScoreboardViewModel(private val repository: BasketballRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ScoreboardUiState())
    val uiState: StateFlow<ScoreboardUiState> = _uiState.asStateFlow()
    private var gamesJob: Job? = null

    init { observeGames(); refreshGames() }

    private fun observeGames() {
        gamesJob?.cancel()
        gamesJob = viewModelScope.launch {
            val gender = if (_uiState.value.isWomens) "women" else "men"
            repository.getGames(_uiState.value.selectedDate, gender)
                .collect { games -> _uiState.update { it.copy(games = games) } }
        }
    }

    fun setDate(date: LocalDate) { _uiState.update { it.copy(selectedDate = date) }; observeGames(); refreshGames() }
    fun setWomens(isWomens: Boolean) { _uiState.update { it.copy(isWomens = isWomens) }; observeGames(); refreshGames() }
    fun refresh() { refreshGames() }

    private fun refreshGames() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val gender = if (_uiState.value.isWomens) "women" else "men"
            val result = repository.refreshGames(_uiState.value.selectedDate, gender)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message?.let { msg ->
                        if (msg.contains("No network")) "Offline – showing cached data" else "Error: $msg"
                    }
                )
            }
        }
    }

    class Factory(private val repository: BasketballRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ScoreboardViewModel(repository) as T
    }
}