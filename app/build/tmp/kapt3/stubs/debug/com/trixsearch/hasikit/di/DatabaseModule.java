package com.trixsearch.hasikit.di;

import android.content.Context;
import androidx.room.Room;
import com.trixsearch.hasikit.data.local.HasikitDatabase;
import com.trixsearch.hasikit.data.local.dao.VideoDao;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

@dagger.Module()
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u00020\u00052\b\b\u0001\u0010\u0006\u001a\u00020\u0007H\u0007J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0005H\u0007\u00a8\u0006\u000b"}, d2 = {"Lcom/trixsearch/hasikit/di/DatabaseModule;", "", "<init>", "()V", "provideDatabase", "Lcom/trixsearch/hasikit/data/local/HasikitDatabase;", "context", "Landroid/content/Context;", "provideVideoDao", "Lcom/trixsearch/hasikit/data/local/dao/VideoDao;", "database", "app_debug"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public final class DatabaseModule {
    @org.jetbrains.annotations.NotNull()
    public static final com.trixsearch.hasikit.di.DatabaseModule INSTANCE = null;
    
    private DatabaseModule() {
        super();
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.trixsearch.hasikit.data.local.HasikitDatabase provideDatabase(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.trixsearch.hasikit.data.local.dao.VideoDao provideVideoDao(@org.jetbrains.annotations.NotNull()
    com.trixsearch.hasikit.data.local.HasikitDatabase database) {
        return null;
    }
}