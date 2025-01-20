package jp.co.yumemi.android.code_check.features.github

import jp.co.yumemi.android.code_check.features.github.api.GitHubServiceApi
import jp.co.yumemi.android.code_check.features.github.entity.RepositoryItem
import jp.co.yumemi.android.code_check.features.github.entity.RepositoryList
import jp.co.yumemi.android.code_check.features.github.entity.RepositoryOwner
import jp.co.yumemi.android.code_check.features.github.reposiotory.GitHubServiceRepositoryImpl
import jp.co.yumemi.android.code_check.features.github.utils.GitHubError
import jp.co.yumemi.android.code_check.features.github.utils.NetworkResult
import kotlinx.coroutines.runBlocking
import org.json.JSONException
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.Before
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import retrofit2.HttpException
import java.io.IOException

class GitHubServiceRepositoryImplTest {
    private lateinit var api: GitHubServiceApi
    private lateinit var repository: GitHubServiceRepositoryImpl

    @Before
    fun setUp() {
        api = mock(GitHubServiceApi::class.java)
        repository = GitHubServiceRepositoryImpl(api)
    }

    @Test
    fun `fetchSearchResults returns success with valid data`() = runBlocking {
        // Arrange
        val mockResponse = GitHubMockData.getMockRepositoryList()
        `when`(api.getRepositoryList("test")).thenReturn(mockResponse)

        // Act
        val result = repository.fetchSearchResults("test")

        // Assert
        assert(result is NetworkResult.Success)
        val success = result as NetworkResult.Success
        assertEquals(2, success.data.size)
        assertEquals("repo1", success.data[0].name)
        assertEquals("url1", success.data[0].ownerIconUrl) // 修正: ownerIconUrl -> avatarUrl
    }


    @Test
    fun `fetchSearchResults returns error on HttpException`() = runBlocking {
        // Arrange
        val httpException = mock(HttpException::class.java)
        `when`(httpException.code()).thenReturn(401)
        `when`(api.getRepositoryList("test")).thenThrow(httpException)

        // Act
        val result = repository.fetchSearchResults("test")

        // Assert
        assert(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assert(error.exception is GitHubError.AuthenticationError)
    }

    @Test
    fun `fetchSearchResults returns error on IOException`() = runBlocking {
        // Arrange
        `when`(api.getRepositoryList("test")).thenAnswer {
            throw IOException("Network error")
        }

        // Act
        val result = repository.fetchSearchResults("test")

        // Assert
        assert(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assert(error.exception is GitHubError.NetworkError)
    }

    @Test
    fun `fetchSearchResults returns error on JSONException`() = runBlocking {
        // Arrange
        `when`(api.getRepositoryList("test")).thenAnswer {
            throw JSONException("Parsing error")
        }

        // Act
        val result = repository.fetchSearchResults("test")

        // Assert
        assert(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assert(error.exception is GitHubError.ParseError)
    }

    @Test
    fun `fetchRepositoryDetail returns success with valid data`() = runBlocking {
        // Arrange
        val mockDetail = GitHubMockData.getMockRepositoryItem()
        `when`(api.getRepositoryDetail(1)).thenReturn(mockDetail)

        // Act
        val result = repository.fetchRepositoryDetail(1)

        // Assert
        assert(result is NetworkResult.Success)
        val success = result as NetworkResult.Success
        assertEquals("repo1", success.data.name)
        assertEquals("url1", success.data.ownerIconUrl)
    }

    @Test
    fun `fetchRepositoryDetail returns error on HttpException`() = runBlocking {
        // Arrange
        val httpException = mock(HttpException::class.java)
        `when`(httpException.code()).thenReturn(429)
        `when`(api.getRepositoryDetail(1)).thenThrow(httpException)

        // Act
        val result = repository.fetchRepositoryDetail(1)

        // Assert
        assert(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assert(error.exception is GitHubError.RateLimitError)
    }

    @Test
    fun `fetchRepositoryDetail returns error on IOException`() = runBlocking {
        // Arrange
        `when`(api.getRepositoryDetail(1)).thenAnswer {
            throw IOException("Network error")
        }

        // Act
        val result = repository.fetchRepositoryDetail(1)

        // Assert
        assert(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assert(error.exception is GitHubError.NetworkError)
    }
}
