package com.screentime.reward.presentation.screen.adult.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.screentime.reward.domain.model.Task
import com.screentime.reward.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdultUiState(
    val pendingTasks: List<Task> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class AdultViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AdultUiState())
    val uiState: StateFlow<AdultUiState> = _uiState.asStateFlow()
    
    init {
        viewModelScope.launch {
            repository.setCurrentRole(com.screentime.reward.domain.model.UserRole.ADULT)
        }
        
        viewModelScope.launch {
            repository.getPendingTasks().collect { tasks ->
                _uiState.update {
                    it.copy(pendingTasks = tasks)
                }
            }
        }
    }
    
    fun approveTask(taskId: Long) {
        viewModelScope.launch {
            repository.approveTask(taskId)
        }
    }
    
    fun rejectTask(taskId: Long) {
        viewModelScope.launch {
            repository.rejectTask(taskId)
        }
    }
}

