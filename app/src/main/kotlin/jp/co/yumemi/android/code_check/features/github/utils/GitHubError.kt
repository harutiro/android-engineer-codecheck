package jp.co.yumemi.android.code_check.features.github.utils

sealed class GitHubError {
    data class NetworkError(val exception: Exception) : GitHubError()

    data class ApiError(val code: Int, val message: String) : GitHubError()

    data class ParseError(val exception: Exception) : GitHubError()

    data object RateLimitError : GitHubError()

    data object AuthenticationError : GitHubError()
}
