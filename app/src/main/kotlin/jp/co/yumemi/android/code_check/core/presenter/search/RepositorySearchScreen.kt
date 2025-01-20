package jp.co.yumemi.android.code_check.core.presenter.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Search
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.core.entity.RepositoryEntity
import jp.co.yumemi.android.code_check.core.presenter.theme.CodeCheckAppTheme
import jp.co.yumemi.android.code_check.core.presenter.widget.ProgressCycle
import jp.co.yumemi.android.code_check.core.utils.DialogHelper

@Composable
fun RepositorySearchScreen(
    toDetailScreen: (Int) -> Unit,
    viewModel: RepositorySearchViewModel = hiltViewModel(),
) {
    var inputText by remember { mutableStateOf("") }
    val repositoryList = remember { mutableStateListOf<RepositoryEntity>() }
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.searchResults.observe(lifecycleOwner) {
            repositoryList.clear()
            repositoryList.addAll(it)
            isLoading = false
        }
        viewModel.errorMessage.observe(lifecycleOwner) {
            it?.let {
                DialogHelper.showErrorDialog(
                    context,
                    context.getString(it),
                )
            }
            isLoading = false
        }
    }

    Column {
        CustomSearchBar(
            inputText = inputText,
            onValueChange = { inputText = it },
            searchAction = { searchWord ->
                repositoryList.clear()
                viewModel.searchRepositories(searchWord.trim())
                isLoading = true
            },
        )
        if (isLoading) {
            ProgressCycle()
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
                contentDescription = null,
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
                ),
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
