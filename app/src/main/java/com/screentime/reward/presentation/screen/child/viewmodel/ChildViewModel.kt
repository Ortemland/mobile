package com.screentime.reward.presentation.screen.child.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.screentime.reward.domain.model.Task
import com.screentime.reward.domain.model.TimeInfo
import com.screentime.reward.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChildUiState(
    val tasks: List<Task> = emptyList(),
    val timeInfo: TimeInfo? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class ChildViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ChildUiState())
    val uiState: StateFlow<ChildUiState> = _uiState.asStateFlow()
    
    init {
        viewModelScope.launch {
            repository.setCurrentRole(com.screentime.reward.domain.model.UserRole.CHILD)
        }
        
        viewModelScope.launch {
            combine(
                repository.getChildTasks(),
                repository.getTimeInfo()
            ) { tasks, timeInfo ->
                _uiState.update {
                    it.copy(
                        tasks = tasks,
                        timeInfo = timeInfo
                    )
                }
            }.collect()
        }
    }
    
    fun addTask(name: String, timeMinutes: Int) {
        viewModelScope.launch {
            repository.createTask(name, timeMinutes)
        }
    }
}

