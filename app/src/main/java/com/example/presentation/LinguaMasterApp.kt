package com.example.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Article
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.core.navigation.ArticleDetailRoute
import com.example.core.navigation.HomeRoute
import com.example.core.navigation.LoginRoute
import com.example.core.navigation.ProfileRoute
import com.example.core.navigation.ReadingRoute
import com.example.core.navigation.VocabularyRoute
import com.example.presentation.auth.LoginScreen
import com.example.presentation.home.HomeScreen
import com.example.presentation.reading.ReadingScreen
import com.example.presentation.reading.ArticleDetailScreen
import com.example.presentation.vocabulary.VocabularyScreen
import com.example.presentation.profile.ProfileScreen

import com.example.core.navigation.AdminRoute
import com.example.presentation.admin.AdminScreen
import com.example.core.navigation.FlashCardRoute
import com.example.presentation.flashcard.FlashCardScreen
import com.example.core.navigation.QuizRoute
import com.example.presentation.vocabulary.QuizScreen

import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LinguaMasterApp(
    mainViewModel: MainViewModel = viewModel(factory = MainViewModel.Factory)
) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = currentDestination?.route != LoginRoute::class.qualifiedName

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = Color.White) {
                    val navColors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFFE0F3F2),
                        selectedIconColor = Color(0xFF006A6A),
                        selectedTextColor = Color(0xFF006A6A),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color(0xFF3F4948)
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == HomeRoute::class.qualifiedName } == true,
                        onClick = {
                            navController.navigate(HomeRoute) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        colors = navColors
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == VocabularyRoute::class.qualifiedName } == true,
                        onClick = {
                            navController.navigate(VocabularyRoute) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Filled.Translate, contentDescription = "Vocabulary") },
                        label = { Text("Vocab") },
                        colors = navColors
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == ReadingRoute::class.qualifiedName } == true,
                        onClick = {
                            navController.navigate(ReadingRoute) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Filled.Article, contentDescription = "Reading") },
                        label = { Text("Reading") },
                        colors = navColors
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == ProfileRoute::class.qualifiedName } == true,
                        onClick = {
                            navController.navigate(ProfileRoute) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                        label = { Text("Profile") },
                        colors = navColors
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = LoginRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<LoginRoute> {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(HomeRoute) {
                            popUpTo(LoginRoute) { inclusive = true }
                        }
                    }
                )
            }
            composable<HomeRoute> { 
                HomeScreen(
                    onFlashCardClick = { navController.navigate(FlashCardRoute) }
                ) 
            }
            composable<VocabularyRoute> { 
                VocabularyScreen(
                    onFlashCardClick = { navController.navigate(QuizRoute) }
                ) 
            }
            composable<ReadingRoute> { 
                ReadingScreen(
                    onArticleClick = { articleId ->
                        navController.navigate(ArticleDetailRoute(articleId))
                    }
                ) 
            }
            composable<ArticleDetailRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<ArticleDetailRoute>()
                ArticleDetailScreen(
                    articleId = route.articleId,
                    onBackClick = { navController.navigateUp() }
                )
            }
            composable<ProfileRoute> { 
                ProfileScreen(
                    onSignOut = {
                        navController.navigate(LoginRoute) {
                            popUpTo(HomeRoute) { inclusive = true }
                        }
                    }
                )
            }
            composable<AdminRoute> { AdminScreen(onBackClick = { navController.navigateUp() }) }
            composable<FlashCardRoute> { FlashCardScreen(onBackClick = { navController.navigateUp() }) }
            composable<QuizRoute> { QuizScreen(onBackClick = { navController.navigateUp() }) }
        }
    }
}
