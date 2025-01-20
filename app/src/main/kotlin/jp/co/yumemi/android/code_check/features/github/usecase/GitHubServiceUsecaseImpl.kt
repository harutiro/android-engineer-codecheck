package jp.co.yumemi.android.code_check.features.github.usecase

import android.content.Context
import jp.co.yumemi.android.code_check.core.entity.RepositoryItem
import jp.co.yumemi.android.code_check.core.api.NetworkConnectivityService
import jp.co.yumemi.android.code_check.features.github.reposiotory.GitHubServiceRepository
import jp.co.yumemi.android.code_check.features.github.reposiotory.NetworkException
import jp.co.yumemi.android.code_check.features.github.utils.NetworkResult
import javax.inject.Inject

class GitHubServiceUsecaseImpl
    @Inject
    constructor(
        private val repository: GitHubServiceRepository,
        private val networkConnectivityService: NetworkConnectivityService,
    ) : GitHubServiceUsecase {
        override suspend fun fetchSearchResults(
            inputText: String,
        ): NetworkResult<List<RepositoryItem>> {
            if (!networkConnectivityService.isNetworkAvailable()) {
                throw NetworkException("オフライン状態です")
            }
            return repository.fetchSearchResults(inputText)
        }
    }
