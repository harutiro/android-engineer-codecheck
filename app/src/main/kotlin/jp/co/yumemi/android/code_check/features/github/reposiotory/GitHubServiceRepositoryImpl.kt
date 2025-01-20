package jp.co.yumemi.android.code_check.features.github.reposiotory

import jp.co.yumemi.android.code_check.core.entity.RepositoryItem
import jp.co.yumemi.android.code_check.features.github.api.GitHubServiceApi
import jp.co.yumemi.android.code_check.features.github.utils.NetworkResult
import org.json.JSONException
import javax.inject.Inject

class GitHubServiceRepositoryImpl
    @Inject
    constructor(
        private val gitHubRepositoryApi: GitHubServiceApi,
    ) : GitHubServiceRepository {
        override suspend fun fetchSearchResults(inputText: String): NetworkResult<List<RepositoryItem>> {
            return try {
                val repositoryList = gitHubRepositoryApi.getRepository(inputText)
                val items =
                    repositoryList.items.map { item ->
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
                NetworkResult.Success(items)
            } catch (e: JSONException) {
                NetworkResult.Error(NetworkException("JSONパースエラー", e))
            } catch (e: Exception) {
                NetworkResult.Error(NetworkException("ネットワークエラー", e))
            }
        }
    }

class NetworkException(message: String, cause: Throwable? = null) : Exception(message, cause)
