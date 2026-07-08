package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Session
import com.example.repository.SessionRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class HomeUiState(
    val currentSessionCount: Int = 0,
    val selectedSection: String = "VARC", // VARC, LRDI, Quant
    val goal: Int? = null,
    val isSaveDialogVisible: Boolean = false
)

class AppViewModel(private val repository: SessionRepository) : ViewModel() {

    val allSessions: StateFlow<List<Session>> = repository.allSessions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    private val _uiEvents = kotlinx.coroutines.flow.MutableSharedFlow<String>()
    val uiEvents = _uiEvents.asSharedFlow()

    fun incrementCounter() {
        _homeUiState.update { it.copy(currentSessionCount = it.currentSessionCount + 1) }
    }

    fun decrementCounter() {
        _homeUiState.update { 
            val newCount = if (it.currentSessionCount > 0) it.currentSessionCount - 1 else 0
            it.copy(currentSessionCount = newCount) 
        }
    }

    fun resetSession() {
        _homeUiState.update { it.copy(currentSessionCount = 0) }
    }

    fun showSaveDialog() {
        _homeUiState.update { it.copy(isSaveDialogVisible = true) }
    }

    fun hideSaveDialog() {
        _homeUiState.update { it.copy(isSaveDialogVisible = false) }
    }

    fun updateSection(section: String) {
        _homeUiState.update { it.copy(selectedSection = section) }
    }

    fun updateGoal(goal: Int?) {
        _homeUiState.update { it.copy(goal = goal) }
    }

    fun saveSession() {
        val state = _homeUiState.value
        if (state.currentSessionCount == 0) {
            hideSaveDialog()
            return
        }

        viewModelScope.launch {
            repository.insertSession(
                Session(
                    date = System.currentTimeMillis(),
                    section = state.selectedSection,
                    questionsSolved = state.currentSessionCount,
                    goal = state.goal
                )
            )
            // Reset after save
            _homeUiState.update { 
                it.copy(
                    currentSessionCount = 0,
                    isSaveDialogVisible = false
                )
            }
            _uiEvents.emit("Session saved.")
        }
    }
}

class AppViewModelFactory(private val repository: SessionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
