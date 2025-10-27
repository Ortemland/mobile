package com.screentime.reward.presentation.screen.adult

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.screentime.reward.domain.model.Task
import com.screentime.reward.presentation.screen.adult.viewmodel.AdultViewModel
import com.screentime.reward.presentation.screen.shared.LinkStatusCard
import com.screentime.reward.data.preferences.LinkPreferences
import com.screentime.reward.domain.model.FamilyLink
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdultCabinetScreen(
    onBackClick: () -> Unit,
    onLinkDevices: () -> Unit = {},
    viewModel: AdultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val linkPreferences = LinkPreferences(LocalContext.current)
    val familyId by linkPreferences.getFamilyIdFlow().collectAsState(initial = null)
    var firebaseLinked by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    // Функция для проверки статуса связки
    suspend fun checkConnectionStatus() {
        val currentFamilyId = linkPreferences.getFamilyId()
        if (currentFamilyId != null) {
            val firebaseRepo = com.screentime.reward.data.firebase.FirebaseSyncRepository()
            try {
                val familySnapshot = firebaseRepo.db.collection("families")
                    .document(currentFamilyId)
                    .get()
                    .await()
                
                val family: FamilyLink? = familySnapshot.toObject(FamilyLink::class.java)
                firebaseLinked = family?.isActive == true
            } catch (e: Exception) {
                firebaseLinked = false
            }
        }
    }
    
    // Проверяем статус связки в Firebase с периодическим обновлением
    LaunchedEffect(Unit) {
        while (true) {
            checkConnectionStatus()
            kotlinx.coroutines.delay(2000) // Проверяем каждые 2 секунды
        }
    }
    
    val isDeviceLinked = firebaseLinked
    
    // Функция принудительного обновления статуса
    val refreshConnection = {
        scope.launch {
            checkConnectionStatus()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Кабинет взрослого") },
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
        }
        ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Карточка статуса связи
            Spacer(modifier = Modifier.height(8.dp))
            LinkStatusCard(
                isLinked = isDeviceLinked,
                onRefresh = refreshConnection
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Заявки на утверждение:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (uiState.pendingTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Нет заявок на утверждение",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn {
                    items(uiState.pendingTasks) { task ->
                        ApprovalTaskItem(
                            task = task,
                            onApprove = { viewModel.approveTask(task.id) },
                            onReject = { viewModel.rejectTask(task.id) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ApprovalTaskItem(
    task: Task,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "+${task.timeMinutes} минут",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Отклонить")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = onApprove
                ) {
                    Text("Утвердить")
                }
            }
        }
    }
}

