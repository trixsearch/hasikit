package com.trixsearch.hasikit.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.trixsearch.hasikit.data.local.dao.VideoDao;
import com.trixsearch.hasikit.data.local.entities.DownloadEntity;
import com.trixsearch.hasikit.data.local.entities.FavoriteEntity;
import com.trixsearch.hasikit.data.local.entities.VideoEntity;
import com.trixsearch.hasikit.data.local.entities.WatchHistoryEntity;
import com.trixsearch.hasikit.data.local.entities.WatchLaterEntity;
import com.trixsearch.hasikit.data.local.entities.WatchProgressEntity;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \u00062\u00020\u0001:\u0001\u0006B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H&\u00a8\u0006\u0007"}, d2 = {"Lcom/trixsearch/hasikit/data/local/HasikitDatabase;", "Landroidx/room/RoomDatabase;", "<init>", "()V", "videoDao", "Lcom/trixsearch/hasikit/data/local/dao/VideoDao;", "Companion", "app_debug"})
@androidx.room.Database(entities = {com.trixsearch.hasikit.data.local.entities.VideoEntity.class, com.trixsearch.hasikit.data.local.entities.WatchProgressEntity.class, com.trixsearch.hasikit.data.local.entities.DownloadEntity.class, com.trixsearch.hasikit.data.local.entities.FavoriteEntity.class, com.trixsearch.hasikit.data.local.entities.WatchLaterEntity.class, com.trixsearch.hasikit.data.local.entities.WatchHistoryEntity.class}, version = 3, exportSchema = false)
public abstract class HasikitDatabase extends androidx.room.RoomDatabase {
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_1_2 = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_2_3 = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.trixsearch.hasikit.data.local.HasikitDatabase.Companion Companion = null;
    
    public HasikitDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.trixsearch.hasikit.data.local.dao.VideoDao videoDao();
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\b\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\u0007\u00a8\u0006\n"}, d2 = {"Lcom/trixsearch/hasikit/data/local/HasikitDatabase$Companion;", "", "<init>", "()V", "MIGRATION_1_2", "Landroidx/room/migration/Migration;", "getMIGRATION_1_2", "()Landroidx/room/migration/Migration;", "MIGRATION_2_3", "getMIGRATION_2_3", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.room.migration.Migration getMIGRATION_1_2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.room.migration.Migration getMIGRATION_2_3() {
            return null;
        }
    }
}