package ru.performanceugc.lr3.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.performanceugc.lr3.data.BriefRepository
import ru.performanceugc.lr3.data.LocalBriefRepository

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideBriefRepository(@ApplicationContext context: Context): BriefRepository =
        LocalBriefRepository(context.getSharedPreferences("studio_briefs", Context.MODE_PRIVATE))
}
