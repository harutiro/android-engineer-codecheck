package jp.co.yumemi.android.code_check.features.github.reposiotory

import jp.co.yumemi.android.code_check.core.entity.RepositoryEntity
import jp.co.yumemi.android.code_check.features.github.utils.NetworkResult

interface GitHubServiceRepository {
    suspend fun fetchSearchResults(inputText: String): NetworkResult<List<RepositoryEntity>>
}
