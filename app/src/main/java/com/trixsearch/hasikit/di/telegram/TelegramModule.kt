package com.trixsearch.hasikit.di.telegram

import com.trixsearch.hasikit.telegram.data.repository.TelegramAuthRepositoryImpl
import com.trixsearch.hasikit.telegram.data.repository.TelegramChannelRepositoryImpl
import com.trixsearch.hasikit.telegram.data.repository.TelegramMediaRepositoryImpl
import com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository
import com.trixsearch.hasikit.telegram.domain.repository.TelegramChannelRepository
import com.trixsearch.hasikit.telegram.domain.repository.TelegramMediaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TelegramModule {

    @Binds @Singleton
    abstract fun bindTelegramAuthRepository(
        impl: TelegramAuthRepositoryImpl
    ): TelegramAuthRepository

    @Binds @Singleton
    abstract fun bindTelegramMediaRepository(
        impl: TelegramMediaRepositoryImpl
    ): TelegramMediaRepository

    @Binds @Singleton
    abstract fun bindTelegramChannelRepository(
        impl: TelegramChannelRepositoryImpl
    ): TelegramChannelRepository
}
