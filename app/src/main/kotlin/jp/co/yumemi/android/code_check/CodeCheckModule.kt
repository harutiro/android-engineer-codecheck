package jp.co.yumemi.android.code_check

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.co.yumemi.android.code_check.features.github.api.GitHubServiceApi
import jp.co.yumemi.android.code_check.features.github.api.GitHubServiceApiImpl
import jp.co.yumemi.android.code_check.features.github.reposiotory.GitHubServiceRepository
import jp.co.yumemi.android.code_check.features.github.reposiotory.GitHubServiceRepositoryImpl
import jp.co.yumemi.android.code_check.features.github.usecase.GitHubServiceUsecase
import jp.co.yumemi.android.code_check.features.github.usecase.GitHubServiceUsecaseImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GitHubUsecaseModule {
    @Singleton
    @Binds
    abstract fun provideGitHubServiceUsecase(impl: GitHubServiceUsecaseImpl): GitHubServiceUsecase
}

@Module
@InstallIn(SingletonComponent::class)
abstract class GitHubRepositoryModule {
    @Singleton
    @Binds
    abstract fun provideGitHubServiceRepository(impl: GitHubServiceRepositoryImpl): GitHubServiceRepository

    companion object {
        @Provides
        @Singleton
        fun provideGitHubServiceApi(): GitHubServiceApi {
            return GitHubServiceApiImpl()
        }
    }
}
