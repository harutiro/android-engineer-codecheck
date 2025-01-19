package jp.co.yumemi.android.code_check.features.github.api

import jp.co.yumemi.android.code_check.features.github.entity.RepositoryList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubRepositoryApiBuilderInterface {
    @GET("/search/repositories")
    suspend fun getRepository(
        @Query("q") searchWord: String,
    ): Response<RepositoryList>
}
