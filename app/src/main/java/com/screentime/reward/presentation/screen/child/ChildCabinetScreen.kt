package com.screentime.reward.presentation.screen.child

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.screentime.reward.domain.model.Task
import com.screentime.reward.domain.model.TaskStatus
import com.screentime.reward.presentation.screen.child.viewmodel.ChildViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildCabinetScreen(
    onBackClick: () -> Unit,
    onLinkDevices: () -> Unit = {},
    viewModel: ChildViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Кабинет ребенка") },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text("Назад", color = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                actions = {
                    TextButton(onClick = onLinkDevices) {
                        Text("Связать", color = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить дело")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Карточка с доступным временем
            TimeCard(
                timeInfo = uiState.timeInfo,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Список дел
            Text(
                text = "Мои дела:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
            )
            
            LazyColumn {
                items(uiState.tasks) { task ->
                    TaskItem(task = task)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
    
    // Диалог добавления нового дела
    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, minutes ->
                viewModel.addTask(name, minutes)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun TimeCard(
    timeInfo: com.screentime.reward.domain.model.TimeInfo?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Доступно времени:",
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = timeInfo?.getTotalTimeString() ?: "...",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun TaskItem(task: Task) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "+${task.timeMinutes} минут",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            StatusChip(status = task.status)
        }
    }
}

@Composable
fun StatusChip(status: TaskStatus) {
    val (text, color) = when (status) {
        TaskStatus.PENDING -> "Ожидает" to MaterialTheme.colorScheme.primary
        TaskStatus.APPROVED -> "Утверждено" to MaterialTheme.colorScheme.primary
        TaskStatus.REJECTED -> "Отклонено" to MaterialTheme.colorScheme.error
    }
    
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 12.sp,
            color = color
        )
    }
}

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit
) {
    var taskName by remember { mutableStateOf("") }
    var timeMinutes by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить дело") },
        text = {
            Column {
                OutlinedTextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = { Text("Название дела") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = timeMinutes,
                    onValueChange = { timeMinutes = it },
                    label = { Text("Время (минуты)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val minutes = timeMinutes.toIntOrNull() ?: 0
                    if (taskName.isNotBlank() && minutes > 0) {
                        onConfirm(taskName, minutes)
                    }
                }
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

