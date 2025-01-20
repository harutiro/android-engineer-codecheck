package jp.co.yumemi.android.code_check.features.github.usecase

import android.content.Context
import jp.co.yumemi.android.code_check.core.entity.RepositoryItem
import jp.co.yumemi.android.code_check.features.github.utils.NetworkResult

interface GitHubServiceUsecase {
    suspend fun fetchSearchResults(
        inputText: String,
        context: Context,
    ): NetworkResult<List<RepositoryItem>>
}
