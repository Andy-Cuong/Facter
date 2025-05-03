package com.andyc.facter

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.andyc.auth.presentation.sign_in.SignInDestination
import com.andyc.auth.presentation.sign_in.SignInScreenRoot
import com.andyc.checker.presentation.check_chat.CheckChatDestination
import com.andyc.checker.presentation.check_chat.CheckChatScreenRoot
import com.andyc.checker.presentation.list.ListDestination
import com.andyc.checker.presentation.list.ListScreenRoot

@Composable
fun NavigationRoot(
    navController: NavHostController,
    isSignedIn: Boolean,
    onSignInClick: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = if (isSignedIn) "checker" else "auth",
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
            )
        }
    ) {
        authGraph(
            navController = navController,
            onSignInClick = onSignInClick
        )
        checkerGraph(
            navController = navController
        )
    }
}

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    onSignInClick: () -> Unit,
) {
    navigation(
        startDestination = SignInDestination.route,
        route = "auth"
    ) {
        composable(route = SignInDestination.route) {
            SignInScreenRoot(
                onSignInClick = onSignInClick,
                onSignInSuccess = {
                    navController.navigate("checker") {
                        popUpTo("auth") {
                            inclusive = true // Avoid coming back to sign in screen upon back press
                        }
                    }
                }
            )
        }
    }
}

fun NavGraphBuilder.checkerGraph(
    navController: NavHostController,
) {
    navigation(
        startDestination = ListDestination.route,
        route = "checker"
    ) {
        composable(route = ListDestination.route) {
            ListScreenRoot(
                onChatClick = { chatId ->
                    navController.navigate("${CheckChatDestination.route}/$chatId")
                },
                onSignOut = { navController.navigate("auth") {
                    popUpTo(ListDestination.route) {
                        inclusive = true
                    }
                }}
            )
        }
        composable(
            route = CheckChatDestination.routeWithArgs,
            arguments = listOf(navArgument(name = CheckChatDestination.CHAT_ID_ARG) {
                type = NavType.StringType
            })
        ) {
            CheckChatScreenRoot(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}