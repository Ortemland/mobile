package com.screentime.reward.presentation.screen.role_selection

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.screentime.reward.domain.model.UserRole

@Composable
fun RoleSelectionScreen(
    onRoleSelected: (UserRole) -> Unit,
    onFamilyLinkNeeded: ((UserRole) -> Unit)? = null
) {
    var showFamilyLink by remember { mutableStateOf<UserRole?>(null) }
    
    if (showFamilyLink != null && onFamilyLinkNeeded != null) {
        // Показываем экран связи
        onFamilyLinkNeeded(showFamilyLink!!)
        return
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Награда за дела",
            fontSize = 32.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 48.dp)
        )
        
        Text(
            text = "Выберите роль:",
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        RoleButton(
            text = "Ребенок",
            onClick = { 
                if (onFamilyLinkNeeded != null) {
                    showFamilyLink = UserRole.CHILD
                } else {
                    onRoleSelected(UserRole.CHILD)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        RoleButton(
            text = "Взрослый",
            onClick = {
                if (onFamilyLinkNeeded != null) {
                    showFamilyLink = UserRole.ADULT
                } else {
                    onRoleSelected(UserRole.ADULT)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun RoleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(
            text = text,
            fontSize = 24.sp
        )
    }
}

