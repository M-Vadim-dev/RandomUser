package com.example.randomuser.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.randomuser.ui.screens.detail.UserDetailScreen
import com.example.randomuser.ui.screens.generate.GenerateScreen
import com.example.randomuser.ui.screens.list.UserListScreen

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController, startDestination = Screen.Generate.route) {
        composable(
            route = Screen.Generate.route,
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween())
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween())
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween())
            }
        ) {
            GenerateScreen(
                onBack = { navController.popBackStack() },
                onGenerate = { gender, nat ->
                    navController.navigate(Screen.List.createRoute(gender, nat))
                })
        }
        composable(
            route = "${Screen.List.route}?${NavArgs.GENDER}={${NavArgs.GENDER}}&${NavArgs.NAT}={${NavArgs.NAT}}",
            arguments = listOf(
                navArgument(NavArgs.GENDER) { defaultValue = NavArgs.DEFAULT_STRING },
                navArgument(NavArgs.NAT) { defaultValue = NavArgs.DEFAULT_STRING }
            ),
            enterTransition = {
                slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween())
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween())
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween())
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween())
            }
        ) {
            UserListScreen(
                onGenerate = { navController.navigate(Screen.Generate.route) },
                onOpenDetail = { id -> navController.navigate(Screen.Detail.createRoute(id)) }
            )
        }
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument(NavArgs.USER_ID) { type = NavType.StringType })
        ) {
            UserDetailScreen(onBack = { navController.popBackStack() })
        }
    }
}
