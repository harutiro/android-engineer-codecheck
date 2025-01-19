package jp.co.yumemi.android.code_check.features.github.reposiotory

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import jp.co.yumemi.android.code_check.RepositoryItem
import jp.co.yumemi.android.code_check.features.github.api.GitHubRepositoryApi
import jp.co.yumemi.android.code_check.features.github.api.GitHubRepositoryApiImpl
import org.json.JSONException

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()

    data class Error(val exception: NetworkException) : NetworkResult<Nothing>()
}

class NetworkRepository(
    private val gitHubRepositoryApi: GitHubRepositoryApi = GitHubRepositoryApiImpl(),
) {
    suspend fun fetchSearchResults(
        inputText: String,
        context: Context,
    ): NetworkResult<List<RepositoryItem>> {
        if (!isNetworkAvailable(context)) {
            return NetworkResult.Error(NetworkException("オフライン"))
        }
        return try {
            val repositoryList = gitHubRepositoryApi.getRepository(inputText)
            val items = repositoryList.items
            NetworkResult.Success(
                items.map { item ->
                    RepositoryItem(
                        name = item.name,
                        ownerIconUrl = item.owner.avatarUrl,
                        language = item.language ?: "none",
                        stargazersCount = item.stargazersCount,
                        watchersCount = item.watchersCount,
                        forksCount = item.forksCount,
                        openIssuesCount = item.openIssuesCount,
                    )
                },
            )
        } catch (e: JSONException) {
            throw NetworkException("JSON解析エラー", e)
        } catch (e: Exception) {
            throw NetworkException("ネットワークエラー", e)
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

class NetworkException(message: String, cause: Throwable? = null) : Exception(message, cause)
