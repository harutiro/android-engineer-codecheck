package jp.co.yumemi.android.code_check.core.presenter.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.sharp.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.core.entity.RepositoryEntity
import jp.co.yumemi.android.code_check.core.presenter.theme.CodeCheckAppTheme
import jp.co.yumemi.android.code_check.core.presenter.widget.ProgressCycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RepositorySearchScreen(
    toDetailScreen: (Int) -> Unit,
    viewModel: RepositorySearchViewModel = hiltViewModel(),
    showSnackBar: (String, Boolean) -> Unit,
) {
    var inputText by remember { mutableStateOf("") }
    val repositoryList = remember { mutableStateListOf<RepositoryEntity>() }
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    // デバウンス処理の追加
    val scope = rememberCoroutineScope()
    var searchJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(Unit) {
        viewModel.searchResults.observe(lifecycleOwner) {
            repositoryList.clear()
            repositoryList.addAll(it)
            isLoading = false
            isError = false
        }
        viewModel.errorMessage.observe(lifecycleOwner) {
            it?.let {
                showSnackBar(
                    context.getString(it),
                    true,
                )
            }
            isLoading = false
            isError = true
        }
    }

    Column {
        CustomSearchBar(
            inputText = inputText,
            onValueChange = { inputText = it },
            searchAction = { searchWord ->
                searchJob?.cancel()
                searchJob =
                    scope.launch {
                        delay(500) // 500ms遅延
                        if (searchWord.isBlank()) return@launch
                        repositoryList.clear()
                        viewModel.searchRepositories(searchWord.trim())
                        isLoading = true
                    }
            },
        )
        if (isLoading) {
            ProgressCycle()
        } else if (repositoryList.isEmpty() && !isError) {
            EmptyState()
        } else if (isError) {
            ErrorState(
                onRetry = {
                    viewModel.searchRepositories(inputText.trim())
                    isLoading = true
                },
            )
        }
        RepositoryListView(
            repositoryList = repositoryList,
            onTapping = toDetailScreen,
        )
    }
}

@Composable
fun RepositoryListView(
    repositoryList: List<RepositoryEntity>,
    onTapping: (Int) -> Unit = {},
) {
    LazyColumn(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .semantics { isTraversalGroup = true },
    ) {
        items(repositoryList.size) { index ->
            Column(
                modifier =
                    Modifier
                        .clickable { onTapping(repositoryList[index].id) }
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
            ) {
                Text(
                    text = repositoryList[index].name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
            }
        }
    }
}

@Composable
fun CustomSearchBar(
    inputText: String = "",
    onValueChange: (String) -> Unit = {},
    searchAction: (String) -> Unit,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // キーボードアクションを定義
    val keyboardActions =
        KeyboardActions(
            onSearch = {
                searchAction(inputText)
                keyboardController?.hide()
            },
        )

    TextField(
        value = inputText,
        onValueChange = onValueChange,
        placeholder = { Text(context.getString(R.string.searchInputText_hint)) },
        leadingIcon = {
            Icon(
                Icons.Sharp.Search,
                contentDescription = context.getString(R.string.search_icon_description),
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(40.dp),
                )
                .semantics {
                    contentDescription = context.getString(R.string.search_bar_description)
                },
        colors =
            TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search,
            ),
        keyboardActions = keyboardActions,
        maxLines = 1,
        singleLine = true,
    )
}

@Composable
fun ErrorState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Error,
            contentDescription = stringResource(R.string.error_icon_description),
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.error_data_fetch_failed),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRetry,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
        ) {
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = stringResource(R.string.empty_state_icon_description),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.empty_state_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.empty_state_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyStatePreview() {
    CodeCheckAppTheme {
        EmptyState()
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorStatePreview() {
    CodeCheckAppTheme {
        ErrorState(onRetry = {})
    }
}

@Composable
@Preview(showBackground = true)
fun CustomSearchBarPreview() {
    CodeCheckAppTheme {
        CustomSearchBar(inputText = "Example") {}
    }
}

@Composable
@Preview(showBackground = true)
fun RepositoryListViewPreview() {
    CodeCheckAppTheme {
        RepositoryListView(
            repositoryList =
                listOf(
                    RepositoryEntity(
                        name = "Jetpack Compose",
                        ownerIconUrl = "",
                        language = "Jetpack Compose",
                        stargazersCount = 1,
                        forksCount = 1,
                        openIssuesCount = 1,
                        watchersCount = 1,
                        id = 1,
                    ),
                ),
            onTapping = {},
        )
    }
}
