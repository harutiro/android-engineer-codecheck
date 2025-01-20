package jp.co.yumemi.android.code_check.features.github

import jp.co.yumemi.android.code_check.core.api.NetworkConnectivityService
import jp.co.yumemi.android.code_check.features.github.reposiotory.GitHubServiceRepository
import jp.co.yumemi.android.code_check.features.github.reposiotory.NetworkException
import jp.co.yumemi.android.code_check.features.github.usecase.GitHubServiceUsecaseImpl
import jp.co.yumemi.android.code_check.features.github.utils.GitHubError
import jp.co.yumemi.android.code_check.features.github.utils.NetworkResult
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class GitHubServiceUsecaseImplTest {
    private lateinit var repository: GitHubServiceRepository
    private lateinit var networkConnectivityService: NetworkConnectivityService
    private lateinit var usecase: GitHubServiceUsecaseImpl

    @Before
    fun setUp() {
        repository = mock(GitHubServiceRepository::class.java)
        networkConnectivityService = mock(NetworkConnectivityService::class.java)
        usecase = GitHubServiceUsecaseImpl(repository, networkConnectivityService)
    }

    @Test
    fun `fetchSearchResults throws NetworkException when offline`() {
        `when`(networkConnectivityService.isNetworkAvailable()).thenReturn(false)

        val exception =
            assertThrows(NetworkException::class.java) {
                runBlocking {
                    usecase.fetchSearchResults("test")
                }
            }

        assertEquals("オフライン状態です", exception.message)
    }

    @Test
    fun `fetchSearchResults returns success when online`() =
        runBlocking {
            val mockResults = GitHubMockData.getMockRepositoryEntityList()

            `when`(networkConnectivityService.isNetworkAvailable()).thenReturn(true)
            `when`(repository.fetchSearchResults("test")).thenReturn(NetworkResult.Success(mockResults))

            val result = usecase.fetchSearchResults("test")

            assertTrue(result is NetworkResult.Success)
            val success = result as NetworkResult.Success
            assertEquals(2, success.data.size)
            assertEquals("repo1", success.data[0].name)
            assertEquals("repo2", success.data[1].name)
        }

    @Test
    fun `fetchSearchResults returns error on API failure`() =
        runBlocking {
            val mockError = GitHubMockData.getMockApiError404()
            `when`(networkConnectivityService.isNetworkAvailable()).thenReturn(true)
            `when`(repository.fetchSearchResults("test")).thenReturn(mockError)

            val result = usecase.fetchSearchResults("test")

            assertTrue(result is NetworkResult.Error)
            val error = result as NetworkResult.Error
            assertTrue(error.exception is GitHubError.ApiError)
            assertEquals(404, (error.exception as GitHubError.ApiError).code)
        }

    @Test
    fun `fetchRepositoryDetail throws NetworkException when offline`() {
        `when`(networkConnectivityService.isNetworkAvailable()).thenReturn(false)

        val exception =
            assertThrows(NetworkException::class.java) {
                runBlocking {
                    usecase.fetchRepositoryDetail(1)
                }
            }

        assertEquals("オフライン状態です", exception.message)
    }

    @Test
    fun `fetchRepositoryDetail returns success when online`() =
        runBlocking {
            val mockDetail = GitHubMockData.getMockRepositoryEntity()

            `when`(networkConnectivityService.isNetworkAvailable()).thenReturn(true)
            `when`(repository.fetchRepositoryDetail(1)).thenReturn(NetworkResult.Success(mockDetail))

            val result = usecase.fetchRepositoryDetail(1)

            assertTrue(result is NetworkResult.Success)
            val success = result as NetworkResult.Success
            assertEquals("repo1", success.data.name)
        }

    @Test
    fun `fetchRepositoryDetail returns error on API failure`() =
        runBlocking {
            val mockError = GitHubMockData.getMockApiError500()
            `when`(networkConnectivityService.isNetworkAvailable()).thenReturn(true)
            `when`(repository.fetchRepositoryDetail(1)).thenReturn(mockError)

            val result = usecase.fetchRepositoryDetail(1)

            assertTrue(result is NetworkResult.Error)
            val error = result as NetworkResult.Error
            assertTrue(error.exception is GitHubError.ApiError)
            assertEquals(500, (error.exception as GitHubError.ApiError).code)
        }

    @Test
    fun `fetchRepositoryDetail handles network error`() =
        runBlocking {
            val mockError = GitHubMockData.getMockNetworkError()
            `when`(networkConnectivityService.isNetworkAvailable()).thenReturn(true)
            `when`(repository.fetchRepositoryDetail(1)).thenReturn(mockError)

            val result = usecase.fetchRepositoryDetail(1)

            assertTrue(result is NetworkResult.Error)
            val error = result as NetworkResult.Error
            assertTrue(error.exception is GitHubError.NetworkError)
        }
}
