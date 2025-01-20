package jp.co.yumemi.android.code_check.features.github.entity

import com.squareup.moshi.Json

data class RepositoryList(
    val items: List<RepositoryItem>,
)

data class RepositoryItem(
    val id: Int,
    val name: String,
    val owner: RepositoryOwner,
    val language: String?,
    @Json(name = "stargazers_count") val stargazersCount: Long,
    @Json(name = "watchers_count") val watchersCount: Long,
    @Json(name = "forks_count") val forksCount: Long,
    @Json(name = "open_issues_count") val openIssuesCount: Long,
)

data class RepositoryOwner(
    @Json(name = "avatar_url") val avatarUrl: String,
)
