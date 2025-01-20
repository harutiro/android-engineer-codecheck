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
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
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
        val mockResponse = mockRepositoryList()
        `when`(api.getRepository("test")).thenReturn(mockResponse)

        // Act
        val result = repository.fetchSearchResults("test")

        // Assert
        assert(result is NetworkResult.Success)
        val success = result as NetworkResult.Success
        assertEquals(2, success.data.size)
        assertEquals("repo1", success.data[0].name)
        assertEquals("owner1", success.data[0].ownerIconUrl)
    }

    @Test
    fun `fetchSearchResults returns error on HttpException`() = runBlocking {
        // Arrange
        val httpException = mock(HttpException::class.java)
        `when`(httpException.code()).thenReturn(401)
        `when`(api.getRepository("test")).thenThrow(httpException)

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
        `when`(api.getRepository("test")).thenAnswer {
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
        `when`(api.getRepository("test")).thenAnswer {
            throw JSONException("Parsing error")
        }

        // Act
        val result = repository.fetchSearchResults("test")

        // Assert
        assert(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assert(error.exception is GitHubError.ParseError)
    }


    // Mockデータ生成関数
    private fun mockRepositoryList(): RepositoryList {
        return RepositoryList(
            items = listOf(
                RepositoryItem(
                    name = "repo1",
                    owner = RepositoryOwner("owner1"),
                    language = "Kotlin",
                    stargazersCount = 100,
                    watchersCount = 50,
                    forksCount = 20,
                    openIssuesCount = 5
                ),
                RepositoryItem(
                    name = "repo2",
                    owner = RepositoryOwner("owner2"),
                    language = "Java",
                    stargazersCount = 200,
                    watchersCount = 80,
                    forksCount = 30,
                    openIssuesCount = 10
                )
            )
        )
    }
}

