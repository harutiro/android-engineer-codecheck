package jp.co.yumemi.android.code_check.core.presenter.router


import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import jp.co.yumemi.android.code_check.core.presenter.detail.RepositoryDetailScreen
import jp.co.yumemi.android.code_check.core.presenter.search.RepositorySearchScreen

@Composable
fun MainRouter(
    toDetailScreen: () -> Unit,
    toBackScreen: () -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavigationBarRoute.SEARCH.route,
        modifier = modifier.fillMaxSize()
    ) {
        composable(BottomNavigationBarRoute.SEARCH.route) {
            RepositorySearchScreen(
                toDetailScreen = toDetailScreen
            )
        }
        composable(BottomNavigationBarRoute.DETAIL.route) {
            RepositoryDetailScreen(
                toBack = toBackScreen
            )
        }
    }
}

enum class BottomNavigationBarRoute(val route: String,val title:String) {
    SEARCH("search","検索"),
    DETAIL("detail","詳細"),
}