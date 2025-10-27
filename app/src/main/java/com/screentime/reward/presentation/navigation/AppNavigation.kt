package com.screentime.reward.presentation.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.screentime.reward.domain.model.UserRole
import com.screentime.reward.presentation.screen.role_selection.RoleSelectionScreen
import com.screentime.reward.presentation.screen.child.ChildCabinetScreen
import com.screentime.reward.presentation.screen.adult.AdultCabinetScreen
import com.screentime.reward.presentation.screen.family.FamilyLinkScreen
import com.screentime.reward.data.preferences.LinkPreferences
import com.screentime.reward.data.firebase.FirebaseSyncRepository
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "role_selection"
    ) {
        composable("role_selection") {
            RoleSelectionScreen(
                onRoleSelected = { role ->
                    when (role) {
                        UserRole.CHILD -> navController.navigate("child_cabinet")
                        UserRole.ADULT -> navController.navigate("adult_cabinet")
                    }
                }
            )
        }
        
        composable("child_cabinet") {
            ChildCabinetScreen(
                onBackClick = {
                    navController.navigate("role_selection") {
                        popUpTo("role_selection") { inclusive = true }
                    }
                },
                onLinkDevices = {
                    navController.navigate("family_link/CHILD")
                }
            )
        }
        
        composable("adult_cabinet") {
            AdultCabinetScreen(
                onBackClick = {
                    navController.navigate("role_selection") {
                        popUpTo("role_selection") { inclusive = true }
                    }
                },
                onLinkDevices = {
                    navController.navigate("family_link/ADULT")
                }
            )
        }
        
        composable("family_link/{role}") { backStackEntry ->
            val roleString = backStackEntry.arguments?.getString("role") ?: "CHILD"
            val role = when (roleString) {
                "ADULT" -> UserRole.ADULT
                "CHILD" -> UserRole.CHILD
                else -> UserRole.CHILD
            }
            
            // Генерируем код для взрослого, для ребенка - показываем ввод
            var connectionCode by remember { mutableStateOf<String?>(null) }
            var isLoading by remember { mutableStateOf(false) }
            
            val firebaseRepo = FirebaseSyncRepository()
            val linkPreferences = LinkPreferences(LocalContext.current)
            val scope = rememberCoroutineScope()
            
            LaunchedEffect(role) {
                if (role == UserRole.ADULT && connectionCode == null) {
                    val code = firebaseRepo.generateConnectionCode()
                    connectionCode = code
                    // Создаем семью в Firebase
                    scope.launch {
                        isLoading = true
                        firebaseRepo.createFamily(code)
                        isLoading = false
                    }
                }
            }
            
            FamilyLinkScreen(
                role = role,
                connectionCode = connectionCode,
                isLoading = isLoading,
                onCodeEntered = { code ->
                    scope.launch {
                        isLoading = true
                        val success = firebaseRepo.joinFamily(code)
                        isLoading = false
                        
                        if (success) {
                            linkPreferences.setLinked(true)
                            navController.popBackStack()
                        } else {
                            // Показать ошибку - код неверный
                        }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
