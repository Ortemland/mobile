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
import com.screentime.reward.domain.model.FamilyLink
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
            var errorMessage by remember { mutableStateOf<String?>(null) }
            
            val linkPreferences = LinkPreferences(LocalContext.current)
            val scope = rememberCoroutineScope()
            
            LaunchedEffect(role) {
                if (role == UserRole.ADULT && connectionCode == null) {
                    try {
                        val firebaseRepo = FirebaseSyncRepository()
                        val code = firebaseRepo.generateConnectionCode()
                        connectionCode = code
                        // Создаем семью в Firebase
                        scope.launch {
                            isLoading = true
                            val familyId = firebaseRepo.createFamilySync(code)
                            linkPreferences.setFamilyId(familyId)
                            linkPreferences.setLinked(true)
                            isLoading = false
                        }
                    } catch (e: Exception) {
                        errorMessage = "Ошибка: ${e.message}"
                    }
                }
            }
            
            FamilyLinkScreen(
                role = role,
                connectionCode = connectionCode,
                isLoading = isLoading,
                errorMessage = errorMessage,
                onCodeEntered = { code ->
                    scope.launch {
                        try {
                            isLoading = true
                            val firebaseRepo = FirebaseSyncRepository()
                            val success = firebaseRepo.joinFamily(code)
                            
                            if (success) {
                                // Получаем familyId из Firebase для ребенка
                                val query = firebaseRepo.db.collection("families")
                                    .whereEqualTo("connectionCode", code)
                                    .limit(1)
                                    .get()
                                    .await()
                                
                                if (!query.isEmpty) {
                                    val family = query.documents.first().toObject(FamilyLink::class.java)
                                    if (family != null) {
                                        linkPreferences.setFamilyId(family.familyId)
                                        linkPreferences.setLinked(true)
                                    }
                                }
                                
                                isLoading = false
                                navController.popBackStack()
                            } else {
                                isLoading = false
                                errorMessage = "Код неверный или уже использован"
                            }
                        } catch (e: Exception) {
                            isLoading = false
                            errorMessage = "Ошибка: ${e.message}"
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
