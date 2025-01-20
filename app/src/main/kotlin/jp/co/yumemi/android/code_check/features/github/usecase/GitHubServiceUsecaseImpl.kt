package jp.co.yumemi.android.code_check.features.github.usecase

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import jp.co.yumemi.android.code_check.core.entity.RepositoryItem
import jp.co.yumemi.android.code_check.features.github.reposiotory.GitHubServiceRepository
import jp.co.yumemi.android.code_check.features.github.reposiotory.NetworkException
import jp.co.yumemi.android.code_check.features.github.utils.NetworkResult
import javax.inject.Inject

class GitHubServiceUsecaseImpl
    @Inject
    constructor(
        private val repository: GitHubServiceRepository,
    ) : GitHubServiceUsecase {
        override suspend fun fetchSearchResults(
            inputText: String,
            context: Context,
        ): NetworkResult<List<RepositoryItem>> {
            if (!isNetworkAvailable(context)) {
                throw NetworkException("オフライン状態です")
            }
            return repository.fetchSearchResults(inputText)
        }

        private fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
    }
