package com.trixsearch.hasikit.di.telegram;

import com.trixsearch.hasikit.telegram.data.repository.TelegramAuthRepositoryImpl;
import com.trixsearch.hasikit.telegram.data.repository.TelegramChannelRepositoryImpl;
import com.trixsearch.hasikit.telegram.data.repository.TelegramMediaRepositoryImpl;
import com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository;
import com.trixsearch.hasikit.telegram.domain.repository.TelegramChannelRepository;
import com.trixsearch.hasikit.telegram.domain.repository.TelegramMediaRepository;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

@dagger.Module()
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\'\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\'J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\u0006\u001a\u00020\nH\'J\u0010\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0006\u001a\u00020\rH\'\u00a8\u0006\u000e"}, d2 = {"Lcom/trixsearch/hasikit/di/telegram/TelegramModule;", "", "<init>", "()V", "bindTelegramAuthRepository", "Lcom/trixsearch/hasikit/telegram/domain/repository/TelegramAuthRepository;", "impl", "Lcom/trixsearch/hasikit/telegram/data/repository/TelegramAuthRepositoryImpl;", "bindTelegramMediaRepository", "Lcom/trixsearch/hasikit/telegram/domain/repository/TelegramMediaRepository;", "Lcom/trixsearch/hasikit/telegram/data/repository/TelegramMediaRepositoryImpl;", "bindTelegramChannelRepository", "Lcom/trixsearch/hasikit/telegram/domain/repository/TelegramChannelRepository;", "Lcom/trixsearch/hasikit/telegram/data/repository/TelegramChannelRepositoryImpl;", "app_debug"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public abstract class TelegramModule {
    
    public TelegramModule() {
        super();
    }
    
    @dagger.Binds()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public abstract com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository bindTelegramAuthRepository(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.data.repository.TelegramAuthRepositoryImpl impl);
    
    @dagger.Binds()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public abstract com.trixsearch.hasikit.telegram.domain.repository.TelegramMediaRepository bindTelegramMediaRepository(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.data.repository.TelegramMediaRepositoryImpl impl);
    
    @dagger.Binds()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public abstract com.trixsearch.hasikit.telegram.domain.repository.TelegramChannelRepository bindTelegramChannelRepository(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.telegram.data.repository.TelegramChannelRepositoryImpl impl);
}