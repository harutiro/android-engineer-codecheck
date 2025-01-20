package jp.co.yumemi.android.code_check.features.github.reposiotory

import jp.co.yumemi.android.code_check.core.entity.RepositoryEntity
import jp.co.yumemi.android.code_check.features.github.api.GitHubServiceApi
import jp.co.yumemi.android.code_check.features.github.utils.GitHubError
import jp.co.yumemi.android.code_check.features.github.utils.NetworkResult
import org.json.JSONException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GitHubServiceRepositoryImpl
    @Inject
    constructor(
        private val gitHubRepositoryApi: GitHubServiceApi,
    ) : GitHubServiceRepository {
        override suspend fun fetchSearchResults(inputText: String): NetworkResult<List<RepositoryEntity>> {
            return try {
                val repositoryList = gitHubRepositoryApi.getRepository(inputText)
                val items =
                    repositoryList.items.map { item ->
                        RepositoryEntity(
                            name = item.name,
                            ownerIconUrl = item.owner.avatarUrl,
                            language = item.language ?: "none",
                            stargazersCount = item.stargazersCount,
                            watchersCount = item.watchersCount,
                            forksCount = item.forksCount,
                            openIssuesCount = item.openIssuesCount,
                            id = item.id,
                        )
                    }
                NetworkResult.Success(items)
            } catch (e: HttpException) {
                val error =
                    when (e.code()) {
                        429 -> GitHubError.RateLimitError
                        401 -> GitHubError.AuthenticationError
                        else -> GitHubError.ApiError(e.code(), e.message())
                    }
                NetworkResult.Error(error)
            } catch (e: JSONException) {
                NetworkResult.Error(GitHubError.ParseError(e))
            } catch (e: IOException) {
                NetworkResult.Error(GitHubError.NetworkError(e))
            }
        }
    }

class NetworkException(message: String, cause: Throwable? = null) : Exception(message, cause)
