package com.lchau1017.cc.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    @Named("io")
    fun provideCoroutineDispatcherIO(): CoroutineDispatcher {
        return Dispatchers.IO
    }


    @Provides
    @Singleton
    @Named("default")
    fun provideCoroutineDispatcherDefault(): CoroutineDispatcher {
        return Dispatchers.Default
    }

}
