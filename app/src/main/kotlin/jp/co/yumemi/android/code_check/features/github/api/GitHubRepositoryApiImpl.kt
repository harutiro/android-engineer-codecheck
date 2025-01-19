package jp.co.yumemi.android.code_check.features.github.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import jp.co.yumemi.android.code_check.BuildConfig
import jp.co.yumemi.android.code_check.features.github.entity.RepositoryList
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class GitHubRepositoryApiImpl : GitHubRepositoryApi {
    override suspend fun getRepository(searchWord: String): RepositoryList {
        val logging = HttpLoggingInterceptor()
        logging.level =
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }

        val client =
            OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

        val moshi =
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

        val weatherService =
            Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(GitHubRepositoryApiBuilderInterface::class.java)

        val response = weatherService.getRepository(searchWord)

        return if (response.isSuccessful && response.body() != null) {
            response.body() ?: RepositoryList(emptyList())
        } else {
            throw Exception("検索を行うことができませんでした。再度試してください")
        }
    }
}
