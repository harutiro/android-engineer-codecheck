package jp.co.yumemi.android.code_check

import android.content.Context
import androidx.lifecycle.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import jp.co.yumemi.android.code_check.TopActivity.Companion.lastSearchDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.Date

/**
 * RepositorySearchFragmentで利用するリポジトリ検索用のViewModel
 */
class RepositorySearchViewModel(
    private val context: Context,
) : ViewModel() {
    /**
     * GitHub APIを利用して検索結果を取得します。
     *
     * @param inputText 検索クエリ
     * @return RepositoryItemのリスト
     */
    suspend fun fetchSearchResults(inputText: String): List<RepositoryItem> {
        val client = HttpClient(Android)

        return withContext(Dispatchers.IO) {
            // APIリクエストを送信
            val response: HttpResponse =
                client.get("https://api.github.com/search/repositories") {
                    header("Accept", "application/vnd.github.v3+json")
                    parameter("q", inputText)
                }

            // レスポンスをJSONとしてパース
            val jsonBody = JSONObject(response.readText())
            val jsonItems = jsonBody.optJSONArray("items") ?: return@withContext emptyList()

            // JSON配列をリストに変換
            parseRepositoryItems(jsonItems)
        }
    }

    /**
     * JSON配列をRepositoryItemのリストに変換します。
     *
     * @param jsonItems JSON配列
     * @return RepositoryItemのリスト
     */
    private fun parseRepositoryItems(jsonItems: JSONArray): List<RepositoryItem> {
        return (0 until jsonItems.length()).mapNotNull { index ->
            val jsonItem = jsonItems.optJSONObject(index) ?: return@mapNotNull null

            val name = jsonItem.optString("full_name")
            val ownerIconUrl = jsonItem.optJSONObject("owner")?.optString("avatar_url") ?: ""
            val language = jsonItem.optString("language")
            val stargazersCount = jsonItem.optLong("stargazers_count")
            val watchersCount = jsonItem.optLong("watchers_count")
            val forksCount = jsonItem.optLong("forks_count")
            val openIssuesCount = jsonItem.optLong("open_issues_count")

            RepositoryItem(
                name = name,
                ownerIconUrl = ownerIconUrl,
                language = context.getString(R.string.written_language, language),
                stargazersCount = stargazersCount,
                watchersCount = watchersCount,
                forksCount = forksCount,
                openIssuesCount = openIssuesCount,
            )
        }.also {
            // 最終検索日時を更新
            lastSearchDate = Date()
        }
    }
}
