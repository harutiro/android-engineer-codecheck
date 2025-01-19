package jp.co.yumemi.android.code_check

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * RepositorySearchFragmentで利用するリポジトリ検索用のViewModel
 */
class RepositorySearchViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext = application

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val client = HttpClient(Android) {
        engine {
            connectTimeout = 10_000
            socketTimeout = 10_000
        }
    }

    override fun onCleared() {
        super.onCleared()
        client.close()
    }

    /**
     * GitHub APIを利用して検索結果を取得します。
     *
     * @param inputText 検索クエリ
     * @return RepositoryItemのリスト
     */
    suspend fun fetchSearchResults(inputText: String): List<RepositoryItem> {
        return withContext(Dispatchers.IO) {
            // APIリクエストを送信
            try {
                val response: HttpResponse = client.get("https://api.github.com/search/repositories") {
                    header("Accept", "application/vnd.github.v3+json")
                    parameter("q", inputText)
                }

                // レスポンスをJSONとしてパース
                val jsonBody = JSONObject(response.readText())
                val jsonItems = jsonBody.optJSONArray("items") ?: return@withContext emptyList()

                // JSON配列をリストに変換
                parseRepositoryItems(jsonItems)
            } catch (e: JSONException) {
                _errorMessage.postValue("正しいJsonの形でデータが整形できませんでした。")
                emptyList()
            } catch (e: Exception) {
                _errorMessage.postValue("ネットワークに接続できませんでした。再度お試しください。")
                emptyList()
            }
        }
    }

    /**
     * JSON配列をRepositoryItemのリストに変換します。
     *
     * @param jsonItems JSON配列
     * @return RepositoryItemのリスト
     */
    private fun parseRepositoryItems(
        jsonItems: JSONArray,
    ): List<RepositoryItem> {
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
                language = if (language.isNullOrEmpty()) "Unknown" else appContext.getString(R.string.written_language, language),
                stargazersCount = stargazersCount,
                watchersCount = watchersCount,
                forksCount = forksCount,
                openIssuesCount = openIssuesCount,
            )
        }
    }
}
