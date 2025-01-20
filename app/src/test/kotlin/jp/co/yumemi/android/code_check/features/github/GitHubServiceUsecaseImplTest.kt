package jp.co.yumemi.android.code_check.features.github

import jp.co.yumemi.android.code_check.core.api.NetworkConnectivityService
import jp.co.yumemi.android.code_check.core.entity.RepositoryItem
import jp.co.yumemi.android.code_check.features.github.reposiotory.GitHubServiceRepository
import jp.co.yumemi.android.code_check.features.github.reposiotory.NetworkException
import jp.co.yumemi.android.code_check.features.github.usecase.GitHubServiceUsecaseImpl
import jp.co.yumemi.android.code_check.features.github.utils.NetworkResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class GitHubServiceUsecaseImplTest {

    private lateinit var repository: GitHubServiceRepository
    private lateinit var networkConnectivityService: NetworkConnectivityService
    private lateinit var usecase: GitHubServiceUsecaseImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = mock(GitHubServiceRepository::class.java)
        networkConnectivityService = mock(NetworkConnectivityService::class.java)
        usecase = GitHubServiceUsecaseImpl(repository, networkConnectivityService)
    }

    @Test
    fun `fetchSearchResults throws NetworkException when offline`() {
        `when`(networkConnectivityService.isNetworkAvailable()).thenReturn(false)

        val exception = assertThrows(NetworkException::class.java) {
            runBlocking {
                usecase.fetchSearchResults("test")
            }
        }

        assertEquals("オフライン状態です", exception.message)
    }

    @Test
    fun `fetchSearchResults returns results when online`() = runBlocking {
        val mockResults = listOf(
            RepositoryItem(
                name = "repo1",
                ownerIconUrl = "description1",
                language = "url1",
                stargazersCount = 100,
                forksCount = 50,
                openIssuesCount = 30,
                watchersCount = 70,
            ),
            RepositoryItem(
                name = "repo2",
                ownerIconUrl = "description2",
                language = "url2",
                stargazersCount = 100,
                forksCount = 50,
                openIssuesCount = 30,
                watchersCount = 70,
            )
        )
        `when`(networkConnectivityService.isNetworkAvailable()).thenReturn(true)
        `when`(repository.fetchSearchResults("test")).thenReturn(NetworkResult.Success(mockResults))

        val result = usecase.fetchSearchResults("test")

        assertEquals(NetworkResult.Success(mockResults), result)
    }
}