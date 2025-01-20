package jp.co.yumemi.android.code_check.core.presenter.search

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.yumemi.android.code_check.core.entity.RepositoryItem
import jp.co.yumemi.android.code_check.features.github.reposiotory.NetworkException
import jp.co.yumemi.android.code_check.features.github.usecase.GitHubServiceUsecaseImpl
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
                    val results = networkRepository.fetchSearchResults(query, context)
                    if (results is NetworkResult.Error) {
                        _errorMessage.postValue(results.exception.message)
                        return@launch
                    }
                    if (results is NetworkResult.Success) {
                        _searchResults.postValue(results.data)
                    }
                } catch (e: NetworkException) {
                    Log.e("NetworkException", e.message, e)
                    _errorMessage.postValue(e.message)
                }
            }
        }
    }
