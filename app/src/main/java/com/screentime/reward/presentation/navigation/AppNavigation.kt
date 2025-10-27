package com.screentime.reward.presentation.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.screentime.reward.domain.model.UserRole
import com.screentime.reward.presentation.screen.role_selection.RoleSelectionScreen
import com.screentime.reward.presentation.screen.child.ChildCabinetScreen
import com.screentime.reward.presentation.screen.adult.AdultCabinetScreen

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
                    navController.navigate("role_selection")
                }
            )
        }
        
        composable("adult_cabinet") {
            AdultCabinetScreen(
                onBackClick = {
                    navController.navigate("role_selection")
                }
            )
        }
    }
}
