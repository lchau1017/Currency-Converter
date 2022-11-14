package com.lchau1017.cc.di

import com.lchau1017.cc.data.local.AppConfigImpl
import com.lchau1017.cc.data.repository.RatesRepositoryImpl
import com.lchau1017.cc.domain.local.AppConfig
import com.lchau1017.cc.domain.repository.RatesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class DataModule {


    @Binds
    abstract fun bindRepository(repository: RatesRepositoryImpl): RatesRepository

    @Binds
    abstract fun bindAppConfig(appConfigImpl: AppConfigImpl): AppConfig
}
