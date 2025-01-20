package jp.co.yumemi.android.code_check.core.presenter

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.core.presenter.router.BottomNavigationBarRoute
import jp.co.yumemi.android.code_check.core.presenter.router.MainRouter
import jp.co.yumemi.android.code_check.core.presenter.widget.EmptyCompose
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val appName = context.getString(R.string.app_name)

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    var topBarTitle by remember {
        mutableStateOf(appName)
    }

    val hostState = remember { SnackbarHostState() }
    var isErrorMessage by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val navigationIcon: @Composable () -> Unit =
        if (navBackStackEntry?.destination?.route != BottomNavigationBarRoute.SEARCH.route) {
            {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        } else {
            {
                EmptyCompose()
            }
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
                navigationIcon = navigationIcon

            )
        },
        snackbarHost = {CustomSnackbarHost(hostState = hostState, isErrorMessage = isErrorMessage)}
    ) { innerPadding ->
        MainRouter(
            toDetailScreen = { id ->
                navController.navigate("${BottomNavigationBarRoute.DETAIL.route}/$id")
            },
            toBackScreen = {
                navController.popBackStack()
            },
            changeTopBarTitle = {
                topBarTitle = it
            },
            showSnackbar = { message, isError ->
                scope.launch {
                    isErrorMessage = isError
                    hostState.showSnackbar(message)
                }
            },
            navController = navController,
            modifier =
                Modifier
                    .padding(innerPadding),
        )
    }
}

@Composable
fun CustomSnackbarHost(
    hostState: SnackbarHostState,
    isErrorMessage: Boolean
) {
    SnackbarHost(
        hostState = hostState
    ) { snackbarData ->
        Snackbar(
            snackbarData = snackbarData,
            containerColor = if (isErrorMessage) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primaryContainer,
            contentColor = if (isErrorMessage) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
