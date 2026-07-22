package com.trixsearch.hasikit.di;

import com.trixsearch.hasikit.data.repository.VideoRepositoryImpl;
import com.trixsearch.hasikit.domain.repository.VideoRepository;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

@dagger.Module()
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\'\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\'\u00a8\u0006\b"}, d2 = {"Lcom/trixsearch/hasikit/di/RepositoryModule;", "", "<init>", "()V", "bindVideoRepository", "Lcom/trixsearch/hasikit/domain/repository/VideoRepository;", "videoRepositoryImpl", "Lcom/trixsearch/hasikit/data/repository/VideoRepositoryImpl;", "app_debug"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public abstract class RepositoryModule {
    
    public RepositoryModule() {
        super();
    }
    
    @dagger.Binds()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public abstract com.trixsearch.hasikit.domain.repository.VideoRepository bindVideoRepository(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.data.repository.VideoRepositoryImpl videoRepositoryImpl);
}