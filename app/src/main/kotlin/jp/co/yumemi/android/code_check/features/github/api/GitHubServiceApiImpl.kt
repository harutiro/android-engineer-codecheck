package jp.co.yumemi.android.code_check.features.github.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import jp.co.yumemi.android.code_check.BuildConfig
import jp.co.yumemi.android.code_check.features.github.entity.RepositoryList
import jp.co.yumemi.android.code_check.features.github.reposiotory.NetworkException
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class GitHubServiceApiImpl : GitHubServiceApi {
    companion object {
        val client =
            OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level =
                            if (BuildConfig.DEBUG) {
                                HttpLoggingInterceptor.Level.BODY
                            } else {
                                HttpLoggingInterceptor.Level.NONE
                            }
                    },
                )
                .build()

        val moshi =
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

        val githubService =
            Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(GitHubServiceApiBuilderInterface::class.java)
    }

    override suspend fun getRepositoryList(searchWord: String): RepositoryList {
        val response = githubService.getRepositoryList(searchWord)

        if (!response.isSuccessful) {
            throw when (response.code()) {
                404 -> NetworkException("リポジトリが見つかりませんでした")
                403 -> NetworkException("APIレート制限に達しました")
                500 -> NetworkException("サーバーエラーが発生しました")
                else -> NetworkException("エラーが発生しました: ${response.code()}")
            }
        }
        return response.body() ?: throw NetworkException("レスポンスが空でした")
    }
}
