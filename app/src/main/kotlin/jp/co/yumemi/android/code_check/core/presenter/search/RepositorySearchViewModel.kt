package jp.co.yumemi.android.code_check.core.presenter.search

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.core.entity.RepositoryItem
import jp.co.yumemi.android.code_check.features.github.reposiotory.NetworkException
import jp.co.yumemi.android.code_check.features.github.usecase.GitHubServiceUsecaseImpl
import jp.co.yumemi.android.code_check.features.github.utils.GitHubError
import jp.co.yumemi.android.code_check.features.github.utils.NetworkResult
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * RepositorySearchFragmentで利用するリポジトリ検索用のViewModel
 */
@HiltViewModel
class RepositorySearchViewModel
    @Inject
    constructor(
        private val networkRepository: GitHubServiceUsecaseImpl,
    ) : ViewModel() {
        private val _errorMessage = MutableLiveData<String?>()
        val errorMessage: LiveData<String?> get() = _errorMessage

        private val _searchResults = MutableLiveData<List<RepositoryItem>>()
        val searchResults: LiveData<List<RepositoryItem>> get() = _searchResults

        /**
         * GitHubのレポジトリ検索を行う
         * @param query 検索キーワード
         * @param context コンテキスト
         */
        fun searchRepositories(
            query: String,
            context: Context,
        ) {
            if (query.isBlank()) {
                _errorMessage.postValue("検索キーワードを入力してください。")
                return
            }
            viewModelScope.launch {
                try {
                    val results = networkRepository.fetchSearchResults(query)
                    if (results is NetworkResult.Error) {
                        handleError(results.exception, context)
                        return@launch
                    }
                    if (results is NetworkResult.Success) {
                        _searchResults.postValue(results.data)
                    }
                } catch (e: NetworkException) {
                    Log.e("NetworkException", e.message, e)
                    handleError(GitHubError.NetworkError(e), context)
                }
            }
        }

        /**
         * エラーが発生した時に、Viewに問題を表示するためのもの
         * @param GitHubError エラー情報
         * @param context コンテキスト
         */
        private fun handleError(
            error: GitHubError,
            context: Context,
        ) {
            val messageRes =
                when (error) {
                    is GitHubError.NetworkError -> R.string.network_error
                    is GitHubError.ApiError -> R.string.api_error
                    is GitHubError.ParseError -> R.string.parse_error
                    is GitHubError.RateLimitError -> R.string.rate_limit_error
                    is GitHubError.AuthenticationError -> R.string.auth_error
                }
            _errorMessage.value = context.getString(messageRes)
        }
    }
