package jp.co.yumemi.android.code_check.core.presenter.router

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.core.presenter.detail.RepositoryDetailScreen
import jp.co.yumemi.android.code_check.core.presenter.search.RepositorySearchScreen

@Composable
fun MainRouter(
    toDetailScreen: (Int) -> Unit,
    toBackScreen: () -> Unit,
    changeTopBarTitle: (String) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {

    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = BottomNavigationBarRoute.SEARCH.route,
        modifier = modifier.fillMaxSize(),
    ) {
        composable(BottomNavigationBarRoute.SEARCH.route) {
            RepositorySearchScreen(
                toDetailScreen = toDetailScreen,
            )
            changeTopBarTitle(context.getString(R.string.app_name))
        }
        composable(
            BottomNavigationBarRoute.DETAIL.route + "/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id")
            RepositoryDetailScreen(
                toBack = toBackScreen,
                repositoryId = id ?: 0,
            )
            changeTopBarTitle(context.getString(R.string.detail))
        }
    }
}

enum class BottomNavigationBarRoute(val route: String, val title: Int) {
    SEARCH("search", R.string.search),
    DETAIL("detail", R.string.detail),
}
