package com.screentime.reward.presentation.screen.family

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.screentime.reward.domain.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyLinkScreen(
    role: UserRole,
    connectionCode: String? = null,
    onCodeEntered: (String) -> Unit,
    onBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = when (role) {
                            UserRole.ADULT -> "Связать устройства"
                            UserRole.CHILD -> "Введите код связи"
                            else -> ""
                        }
                    )
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (role == UserRole.ADULT && connectionCode != null) {
                // Взрослый видит код для связи
                AdultConnectionCodeView(connectionCode = connectionCode)
            } else if (role == UserRole.CHILD) {
                // Ребенок вводит код
                ChildCodeInputView(onCodeEntered = onCodeEntered)
            }
        }
    }
}

@Composable
fun AdultConnectionCodeView(connectionCode: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Код связи:",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = connectionCode,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Покажите этот код ребенку,\nчтобы связать устройства",
                fontSize = 14.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
    
    Spacer(modifier = Modifier.height(32.dp))
    
    Text(
        text = "После ввода кода ребенком,\nустройства будут связаны",
        fontSize = 12.sp,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    )
}

@Composable
fun ChildCodeInputView(onCodeEntered: (String) -> Unit) {
    var code by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Попросите взрослого показать\nкод связи",
            fontSize = 18.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = code,
            onValueChange = { 
                if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                    code = it
                }
            },
            label = { Text("Код связи (6 цифр)") },
            placeholder = { Text("000000") },
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { 
                if (code.length == 6) {
                    onCodeEntered(code)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = code.length == 6
        ) {
            Text(
                text = "Связать устройства",
                fontSize = 18.sp
            )
        }
    }
}

