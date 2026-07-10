package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ActiveSessionData
import com.example.data.ActiveSessionPreferences
import com.example.data.Session
import com.example.repository.SessionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val currentSessionCount: Int = 0,
    val selectedSection: String = "VARC", // VARC, LRDI, Quant
    val goal: Int? = null,
    val isLoading: Boolean = true
)

class AppViewModel(
    private val repository: SessionRepository,
    private val activeSessionPreferences: ActiveSessionPreferences
) : ViewModel() {

    val allSessions: StateFlow<List<Session>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalSolved: StateFlow<Int> = repository.totalQuestionsSolved
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val calendar = java.util.Calendar.getInstance()
    
    private val startOfDay = calendar.apply {
        set(java.util.Calendar.HOUR_OF_DAY, 0)
        set(java.util.Calendar.MINUTE, 0)
        set(java.util.Calendar.SECOND, 0)
        set(java.util.Calendar.MILLISECOND, 0)
    }.timeInMillis

    private val endOfDay = calendar.apply {
        set(java.util.Calendar.HOUR_OF_DAY, 23)
        set(java.util.Calendar.MINUTE, 59)
        set(java.util.Calendar.SECOND, 59)
        set(java.util.Calendar.MILLISECOND, 999)
    }.timeInMillis

    private val startOfWeek = calendar.apply {
        timeInMillis = startOfDay
        set(java.util.Calendar.DAY_OF_WEEK, firstDayOfWeek)
    }.timeInMillis

    private val startOfMonth = calendar.apply {
        timeInMillis = startOfDay
        set(java.util.Calendar.DAY_OF_MONTH, 1)
    }.timeInMillis

    private val startOfLastWeek = calendar.apply {
        timeInMillis = startOfWeek
        add(java.util.Calendar.WEEK_OF_YEAR, -1)
    }.timeInMillis

    private val endOfLastWeek = calendar.apply {
        timeInMillis = startOfLastWeek
        add(java.util.Calendar.DAY_OF_YEAR, 6)
        set(java.util.Calendar.HOUR_OF_DAY, 23)
        set(java.util.Calendar.MINUTE, 59)
        set(java.util.Calendar.SECOND, 59)
        set(java.util.Calendar.MILLISECOND, 999)
    }.timeInMillis

    private val startOf7DaysAgo = calendar.apply {
        timeInMillis = startOfDay
        add(java.util.Calendar.DAY_OF_YEAR, -6)
    }.timeInMillis

    val lastWeekSolved: StateFlow<Int> = repository.getTotalQuestionsBetween(startOfLastWeek, endOfLastWeek)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val last7DaysSessions: StateFlow<List<Session>> = repository.getSessionsBetween(startOf7DaysAgo, endOfDay)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todaySolved: StateFlow<Int> = repository.getTotalQuestionsBetween(startOfDay, endOfDay)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val thisWeekSolved: StateFlow<Int> = repository.getTotalQuestionsBetween(startOfWeek, endOfDay)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val thisMonthSolved: StateFlow<Int> = repository.getTotalQuestionsBetween(startOfMonth, endOfDay)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
        
    val varcCount: StateFlow<Int> = repository.getTotalQuestionsBySection("VARC")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
        
    val lrdiCount: StateFlow<Int> = repository.getTotalQuestionsBySection("LRDI")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
        
    val quantCount: StateFlow<Int> = repository.getTotalQuestionsBySection("Quant")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)


    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    private var saveJob: Job? = null

    init {
        viewModelScope.launch {
            val activeSession = activeSessionPreferences.activeSessionFlow.first()
            _homeUiState.update { 
                it.copy(
                    currentSessionCount = activeSession.currentCount,
                    selectedSection = activeSession.selectedSection,
                    goal = activeSession.goal,
                    isLoading = false
                )
            }
        }
    }

    private fun persistSession() {
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(300) // debounce
            val state = _homeUiState.value
            activeSessionPreferences.saveActiveSession(
                ActiveSessionData(state.currentSessionCount, state.selectedSection, state.goal)
            )
        }
    }

    fun incrementCounter(amount: Int = 1) {
        _homeUiState.update { it.copy(currentSessionCount = it.currentSessionCount + amount) }
        persistSession()
    }

    fun decrementCounter() {
        _homeUiState.update { 
            val newCount = if (it.currentSessionCount > 0) it.currentSessionCount - 1 else 0
            it.copy(currentSessionCount = newCount) 
        }
        persistSession()
    }

    fun discardSession() {
        _homeUiState.update { it.copy(currentSessionCount = 0, goal = null) }
        viewModelScope.launch {
            activeSessionPreferences.clearActiveSession()
            _uiEvents.emit(UiEvent.ShowSnackbar("Session discarded."))
        }
    }

    fun updateSection(section: String) {
        _homeUiState.update { it.copy(selectedSection = section) }
        persistSession()
    }

    fun updateGoal(goal: Int?) {
        _homeUiState.update { it.copy(goal = goal) }
        persistSession()
    }

    fun saveSession() {
        val state = _homeUiState.value
        if (state.currentSessionCount == 0) {
            return
        }

        val count = state.currentSessionCount
        val section = state.selectedSection

        viewModelScope.launch {
            repository.insertSession(
                Session(
                    date = System.currentTimeMillis(),
                    section = section,
                    questionsSolved = count,
                    goal = state.goal
                )
            )
            // Reset after save
            _homeUiState.update { 
                it.copy(
                    currentSessionCount = 0,
                    goal = null
                )
            }
            activeSessionPreferences.clearActiveSession()
            _uiEvents.emit(UiEvent.ShowSnackbar("$count $section questions saved."))
        }
    }

    fun updateSession(session: Session) {
        viewModelScope.launch {
            repository.updateSession(session)
            _uiEvents.emit(UiEvent.ShowSnackbar("Session updated."))
        }
    }

    fun deleteSession(session: Session) {
        viewModelScope.launch {
            repository.deleteSession(session)
            _uiEvents.emit(UiEvent.ShowSnackbar("Session deleted.", actionLabel = "Undo", sessionToRestore = session))
        }
    }

    fun restoreSession(session: Session) {
        viewModelScope.launch {
            repository.insertSession(session)
        }
    }
}

sealed class UiEvent {
    data class ShowSnackbar(val message: String, val actionLabel: String? = null, val sessionToRestore: Session? = null) : UiEvent()
}

class AppViewModelFactory(
    private val repository: SessionRepository,
    private val activeSessionPreferences: ActiveSessionPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(repository, activeSessionPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
