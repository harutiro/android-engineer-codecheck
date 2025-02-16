package jp.co.yumemi.android.code_check.features.github.api

import jp.co.yumemi.android.code_check.features.github.entity.RepositoryItem
import jp.co.yumemi.android.code_check.features.github.entity.RepositoryList

interface GitHubServiceApi {
    suspend fun getRepositoryList(searchWord: String): RepositoryList

    suspend fun getRepositoryDetail(id: Int): RepositoryItem
}
