package jp.co.yumemi.android.code_check

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class NetworkRepository(private val client: HttpClient) {
    suspend fun fetchSearchResults(inputText: String): List<RepositoryItem> {
        try {
            val response: HttpResponse =
                client.get("https://api.github.com/search/repositories") {
                    header("Accept", "application/vnd.github.v3+json")
                    parameter("q", inputText)
                }

            val jsonBody = JSONObject(response.readText())
            val jsonItems = jsonBody.optJSONArray("items") ?: return emptyList()

            return parseRepositoryItems(jsonItems)
        } catch (e: JSONException) {
            throw NetworkException("JSON解析エラー", e)
        } catch (e: Exception) {
            throw NetworkException("ネットワークエラー", e)
        }
    }

    private fun parseRepositoryItems(jsonItems: JSONArray): List<RepositoryItem> {
        return (0 until jsonItems.length()).mapNotNull { index ->
            val jsonItem = jsonItems.optJSONObject(index) ?: return@mapNotNull null

            val name = jsonItem.optString("full_name", "Unknown")
            val ownerIconUrl = jsonItem.optJSONObject("owner")?.optString("avatar_url") ?: ""
            val language = jsonItem.optString("language", "Unknown")
            val stargazersCount = jsonItem.optLong("stargazers_count", 0)
            val watchersCount = jsonItem.optLong("watchers_count", 0)
            val forksCount = jsonItem.optLong("forks_count", 0)
            val openIssuesCount = jsonItem.optLong("open_issues_count", 0)

            RepositoryItem(
                name = name,
                ownerIconUrl = ownerIconUrl,
                language = language,
                stargazersCount = stargazersCount,
                watchersCount = watchersCount,
                forksCount = forksCount,
                openIssuesCount = openIssuesCount,
            )
        }
    }

    fun close() {
        client.close()
    }
}

class NetworkException(message: String, cause: Throwable? = null) : Exception(message, cause)