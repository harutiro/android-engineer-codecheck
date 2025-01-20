package jp.co.yumemi.android.code_check.features.github

import jp.co.yumemi.android.code_check.core.api.NetworkConnectivityService
import jp.co.yumemi.android.code_check.core.entity.RepositoryEntity
import jp.co.yumemi.android.code_check.features.github.entity.RepositoryItem
import jp.co.yumemi.android.code_check.features.github.entity.RepositoryList
import jp.co.yumemi.android.code_check.features.github.entity.RepositoryOwner
import jp.co.yumemi.android.code_check.features.github.utils.GitHubError
import jp.co.yumemi.android.code_check.features.github.utils.NetworkResult
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.io.IOException

object GitHubMockData {
    // 正常なリポジトリ検索結果
    fun getMockRepositoryList(): RepositoryList {
        return RepositoryList(
            items =
                listOf(
                    RepositoryItem(
                        name = "repo1",
                        owner =
                            RepositoryOwner(
                                avatarUrl = "url1",
                            ),
                        language = "Kotlin",
                        stargazersCount = 100,
                        forksCount = 50,
                        openIssuesCount = 30,
                        watchersCount = 70,
                        id = 1,
                    ),
                    RepositoryItem(
                        name = "repo2",
                        owner =
                            RepositoryOwner(
                                avatarUrl = "url2",
                            ),
                        language = "Java",
                        stargazersCount = 200,
                        forksCount = 80,
                        openIssuesCount = 20,
                        watchersCount = 60,
                        id = 2,
                    ),
                ),
        )
    }

    fun getMockRepositoryEntityList(): List<RepositoryEntity> {
        return listOf(
            RepositoryEntity(
                name = "repo1",
                ownerIconUrl = "url1",
                language = "Kotlin",
                stargazersCount = 100,
                forksCount = 50,
                openIssuesCount = 30,
                watchersCount = 70,
                id = 1,
            ),
            RepositoryEntity(
                name = "repo2",
                ownerIconUrl = "url2",
                language = "Java",
                stargazersCount = 200,
                forksCount = 80,
                openIssuesCount = 20,
                watchersCount = 60,
                id = 2,
            ),
        )
    }

    // 正常なリポジトリ詳細
    fun getMockRepositoryItem(): RepositoryItem {
        return RepositoryItem(
            name = "repo1",
            language = "Kotlin",
            stargazersCount = 100,
            forksCount = 50,
            openIssuesCount = 30,
            watchersCount = 70,
            id = 1,
            owner =
                RepositoryOwner(
                    avatarUrl = "url1",
                ),
        )
    }

    fun getMockRepositoryEntity(): RepositoryEntity {
        return RepositoryEntity(
            name = "repo1",
            ownerIconUrl = "url1",
            language = "Kotlin",
            stargazersCount = 100,
            forksCount = 50,
            openIssuesCount = 30,
            watchersCount = 70,
            id = 1,
        )
    }

    // ネットワークエラー
    fun getMockNetworkError(): NetworkResult.Error {
        return NetworkResult.Error(GitHubError.NetworkError(IOException("Network issue")))
    }

    // APIエラー (例えば404)
    fun getMockApiError404(): NetworkResult.Error {
        return NetworkResult.Error(GitHubError.ApiError(404, "Not Found"))
    }

    // APIエラー (例えば500)
    fun getMockApiError500(): NetworkResult.Error {
        return NetworkResult.Error(GitHubError.ApiError(500, "Internal Server Error"))
    }

    // オフライン時のネットワーク接続サービス
    fun getMockOfflineNetworkService(): NetworkConnectivityService {
        val networkService = mock(NetworkConnectivityService::class.java)
        `when`(networkService.isNetworkAvailable()).thenReturn(false)
        return networkService
    }

    // オンライン時のネットワーク接続サービス
    fun getMockOnlineNetworkService(): NetworkConnectivityService {
        val networkService = mock(NetworkConnectivityService::class.java)
        `when`(networkService.isNetworkAvailable()).thenReturn(true)
        return networkService
    }
}
