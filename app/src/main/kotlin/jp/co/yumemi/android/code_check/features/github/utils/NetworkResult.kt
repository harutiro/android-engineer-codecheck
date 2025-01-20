package jp.co.yumemi.android.code_check.features.github.utils

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()

    data class Error(val exception: GitHubError) : NetworkResult<Nothing>()
}
