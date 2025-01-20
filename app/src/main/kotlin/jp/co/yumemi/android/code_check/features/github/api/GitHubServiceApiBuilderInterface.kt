package jp.co.yumemi.android.code_check.features.github.api

import jp.co.yumemi.android.code_check.features.github.entity.RepositoryItem
import jp.co.yumemi.android.code_check.features.github.entity.RepositoryList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubServiceApiBuilderInterface {
    @GET("/search/repositories")
    suspend fun getRepositoryList(
        @Query("q") searchWord: String,
    ): Response<RepositoryList>

    @GET("/repositories/{id}")
    suspend fun getRepositoryDetail(
        @Path("id") id: Int,
    ): Response<RepositoryItem>
}
