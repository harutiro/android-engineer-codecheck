package jp.co.yumemi.android.code_check.features.github.reposiotory

import jp.co.yumemi.android.code_check.RepositoryItem
import jp.co.yumemi.android.code_check.features.github.api.GitHubRepositoryApi
import jp.co.yumemi.android.code_check.features.github.api.GitHubRepositoryApiImpl
import org.json.JSONException

class NetworkRepository {
    val gitHubRepositoryApi: GitHubRepositoryApi = GitHubRepositoryApiImpl()

    suspend fun fetchSearchResults(inputText: String): List<RepositoryItem> {
        try {
            val repositoryList = gitHubRepositoryApi.getRepository(inputText)
            val items = repositoryList.items
            return items.map { item ->
                RepositoryItem(
                    name = item.name,
                    ownerIconUrl = item.owner.avatarUrl,
                    language = item.language ?: "none",
                    stargazersCount = item.stargazersCount,
                    watchersCount = item.watchersCount,
                    forksCount = item.forksCount,
                    openIssuesCount = item.openIssuesCount,
                )
            }
        } catch (e: JSONException) {
            throw NetworkException("JSON解析エラー", e)
        } catch (e: Exception) {
            throw NetworkException("ネットワークエラー", e)
        }
    }
}

class NetworkException(message: String, cause: Throwable? = null) : Exception(message, cause)
