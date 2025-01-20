package jp.co.yumemi.android.code_check.core.presenter.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.rememberAsyncImagePainter
import jp.co.yumemi.android.code_check.core.entity.RepositoryEntity
import jp.co.yumemi.android.code_check.core.presenter.widget.ProgressCycle
import jp.co.yumemi.android.code_check.core.utils.DialogHelper

@Composable
fun RepositoryDetailScreen(
    toBack: () -> Unit,
    repositoryId: Int,
    viewModel: RepositoryDetailViewModel = hiltViewModel(),
    showSnackBar: (String, Boolean) -> Unit
) {
    val repositoryDetail = remember { mutableStateOf<RepositoryEntity?>(null) }
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.searchResults.observe(lifecycleOwner) {
            repositoryDetail.value = it
            isLoading = false
        }
        viewModel.errorMessage.observe(lifecycleOwner) { errorMessage ->
            errorMessage?.let {
                showSnackBar(context.getString(it), true)
            }
            isLoading = false
        }
        viewModel.searchRepositories(repositoryId)
    }

    if(repositoryDetail.value == null && !isLoading){
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().fillMaxHeight()
        ){
            Text(text = "データの取得に失敗しました。")

        }
    }

    RepositoryDetailScaffold(
        isLoading = isLoading,
        repositoryDetail = repositoryDetail.value,
        toBack = toBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepositoryDetailScaffold(
    isLoading: Boolean,
    repositoryDetail: RepositoryEntity?,
    toBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        if (isLoading) {
            ProgressCycle()
        } else {
            repositoryDetail?.let {
                RepositoryDetailContent(repository = it)
            }
        }
    }
}

@Composable
fun RepositoryDetailContent(repository: RepositoryEntity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RepositoryOverviewCard(repository = repository)
        RepositoryStatsCard(repository = repository)
    }
}

@Composable
fun RepositoryOverviewCard(repository: RepositoryEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(repository.ownerIconUrl),
                contentDescription = "Owner Icon",
                modifier = Modifier.size(80.dp)
            )
            Text(
                text = repository.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Language: ${repository.language}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun RepositoryStatsCard(repository: RepositoryEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star, // 適切なアイコンを選択
                    contentDescription = "Stars",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Stars: ${repository.stargazersCount}", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Share, // 適切なアイコンを選択
                    contentDescription = "Forks",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Forks: ${repository.forksCount}", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Report, // 適切なアイコンを選択
                    contentDescription = "Open Issues",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open Issues: ${repository.openIssuesCount}", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRepositoryOverviewCard() {
    RepositoryOverviewCard(
        repository = RepositoryEntity(
            id = 1,
            name = "Example Repo",
            ownerIconUrl = "https://via.placeholder.com/150",
            language = "Kotlin",
            stargazersCount = 123,
            forksCount = 45,
            openIssuesCount = 2,
            watchersCount = 1
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewRepositoryStatsCard() {
    RepositoryStatsCard(
        repository = RepositoryEntity(
            id = 1,
            name = "Example Repo",
            ownerIconUrl = "https://via.placeholder.com/150",
            language = "Kotlin",
            stargazersCount = 123,
            forksCount = 45,
            openIssuesCount = 2,
            watchersCount = 1
        )
    )
}
