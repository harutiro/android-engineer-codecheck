package jp.co.yumemi.android.code_check.core.presenter

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.rememberNavController
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.core.presenter.router.BottomNavigationBarRoute
import jp.co.yumemi.android.code_check.core.presenter.router.MainRouter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val appName = context.getString(R.string.app_name)

    val navController = rememberNavController()
    val topBarTitle by remember {
        mutableStateOf(appName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = topBarTitle,
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
            )
        },
    ) { innerPadding ->
        MainRouter(
            toDetailScreen = { id ->
                navController.navigate("${BottomNavigationBarRoute.DETAIL.route}/$id")
            },
            toBackScreen = {
                navController.popBackStack()
            },
            navController = navController,
            modifier =
                Modifier
                    .padding(innerPadding),
        )
    }
}
