package jp.co.yumemi.android.code_check

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import jp.co.yumemi.android.code_check.features.github.reposiotory.NetworkException
import jp.co.yumemi.android.code_check.features.github.reposiotory.NetworkRepository
import kotlinx.coroutines.launch

/**
 * RepositorySearchFragmentで利用するリポジトリ検索用のViewModel
 */
class RepositorySearchViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext = application
    private val networkRepository = NetworkRepository()

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _searchResults = MutableLiveData<List<RepositoryItem>>()
    val searchResults: LiveData<List<RepositoryItem>> get() = _searchResults

    /**
     * GitHubのレポジトリ検索を行う
     * @param query 検索キーワード
     */
    fun searchRepositories(query: String) {
        if (query.isBlank()) {
            _errorMessage.postValue("検索キーワードを入力してください。")
            return
        }
        viewModelScope.launch {
            try {
                val results = networkRepository.fetchSearchResults(query)
                _searchResults.postValue(results)
            } catch (e: NetworkException) {
                Log.e("NetworkException", e.message, e)
                _errorMessage.postValue(e.message)
            }
        }
    }
}
