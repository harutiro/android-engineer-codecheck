package jp.co.yumemi.android.code_check.core.presenter.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.core.entity.RepositoryEntity
import jp.co.yumemi.android.code_check.features.github.reposiotory.NetworkException
import jp.co.yumemi.android.code_check.features.github.usecase.GitHubServiceUsecase
import jp.co.yumemi.android.code_check.features.github.utils.GitHubError
import jp.co.yumemi.android.code_check.features.github.utils.NetworkResult
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepositoryDetailViewModel
    @Inject
    constructor(
        private val networkRepository: GitHubServiceUsecase,
    ) : ViewModel() {
        private val _errorMessage = MutableLiveData<Int?>()
        val errorMessage: LiveData<Int?> get() = _errorMessage

        private val _searchResults = MutableLiveData<RepositoryEntity>()
        val searchResults: LiveData<RepositoryEntity> get() = _searchResults

        /**
         * GitHubのレポジトリ検索を行う
         * @param id 検索キーワード
         */
        fun searchRepositories(id: Int) {
            if (id == 0) {
                _errorMessage.postValue(R.string.invalid_repository_id)
                return
            }
            viewModelScope.launch {
                try {
                    val results = networkRepository.fetchRepositoryDetail(id)
                    if (results is NetworkResult.Error) {
                        handleError(results.exception)
                        return@launch
                    }
                    if (results is NetworkResult.Success) {
                        _searchResults.postValue(results.data)
                    }
                } catch (e: NetworkException) {
                    Log.e("RepositoryDetailViewModel", "Failed to fetch repository details for id: $id", e)
                    handleError(GitHubError.NetworkError(e))
                }
            }
        }

        /**
         * エラーが発生した時に、Viewに問題を表示するためのもの
         * @param error エラー情報
         */
        private fun handleError(error: GitHubError) {
            _errorMessage.value =
                when (error) {
                    is GitHubError.NetworkError -> R.string.network_error
                    is GitHubError.ApiError -> R.string.api_error
                    is GitHubError.ParseError -> R.string.parse_error
                    is GitHubError.RateLimitError -> R.string.rate_limit_error
                    is GitHubError.AuthenticationError -> R.string.auth_error
                }
        }
    }
