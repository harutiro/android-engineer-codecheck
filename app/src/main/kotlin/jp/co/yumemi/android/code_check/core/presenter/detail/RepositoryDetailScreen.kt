package jp.co.yumemi.android.code_check.core.presenter.detail

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun RepositoryDetailScreen(
    toBack: () -> Unit,
    repositoryId: Int,
) {
//    Text(text = "RepositoryDetailScreen")
    Text(text = "repositoryId: $repositoryId")
}
