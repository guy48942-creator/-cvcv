package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.mushaf.MushafScreen
import com.example.ui.screens.reciters.RecitersScreen
import com.example.ui.screens.qibla.QiblaScreen
import com.example.ui.screens.map.MosqueMapScreen
import com.example.ui.screens.bookmarks.BookmarksScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Mushaf : Screen("mushaf")
    object Reciters : Screen("reciters")
    object Qibla : Screen("qibla")
    object MosqueMap : Screen("map")
    object Bookmarks : Screen("bookmarks")
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Mushaf.route) { MushafScreen(navController) }
        composable(Screen.Reciters.route) { RecitersScreen(navController) }
        composable(Screen.Qibla.route) { QiblaScreen(navController) }
        composable(Screen.MosqueMap.route) { MosqueMapScreen(navController) }
        composable(Screen.Bookmarks.route) { BookmarksScreen(navController) }
    }
}
