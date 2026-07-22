package com.trixsearch.hasikit.data.local;

import androidx.annotation.NonNull;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenDelegate;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.SQLite;
import androidx.sqlite.SQLiteConnection;
import com.trixsearch.hasikit.data.local.dao.VideoDao;
import com.trixsearch.hasikit.data.local.dao.VideoDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class HasikitDatabase_Impl extends HasikitDatabase {
  private volatile VideoDao _videoDao;

  @Override
  @NonNull
  protected RoomOpenDelegate createOpenDelegate() {
    final RoomOpenDelegate _openDelegate = new RoomOpenDelegate(3, "eb5c476f74e89b5a10d22c842db08cbf", "d9b2ef5331a3d41b2cf6334767180fe2") {
      @Override
      public void createAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `videos` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `thumbnail` TEXT, `videoUrl` TEXT NOT NULL, `telegramFileId` TEXT NOT NULL, `duration` INTEGER NOT NULL, `size` INTEGER NOT NULL, `localPath` TEXT, `isDownloaded` INTEGER NOT NULL, `downloadProgress` REAL NOT NULL, `sourceLabel` TEXT NOT NULL, `isStreamable` INTEGER NOT NULL, `uploadDate` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `watch_progress` (`videoId` TEXT NOT NULL, `lastPosition` INTEGER NOT NULL, `duration` INTEGER NOT NULL, `lastWatchedAt` INTEGER NOT NULL, PRIMARY KEY(`videoId`))");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `downloads` (`videoId` TEXT NOT NULL, `state` TEXT NOT NULL, `progress` REAL NOT NULL, `localPath` TEXT, `errorCode` INTEGER, `downloadId` INTEGER, PRIMARY KEY(`videoId`))");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `favorites` (`videoId` TEXT NOT NULL, `title` TEXT NOT NULL, `thumbnail` TEXT, `source` TEXT NOT NULL, `addedAt` INTEGER NOT NULL, PRIMARY KEY(`videoId`))");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `watch_later` (`videoId` TEXT NOT NULL, `title` TEXT NOT NULL, `thumbnail` TEXT, `source` TEXT NOT NULL, `addedAt` INTEGER NOT NULL, PRIMARY KEY(`videoId`))");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `watch_history` (`videoId` TEXT NOT NULL, `title` TEXT NOT NULL, `thumbnail` TEXT, `source` TEXT NOT NULL, `watchedAt` INTEGER NOT NULL, PRIMARY KEY(`videoId`))");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        SQLite.execSQL(connection, "INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'eb5c476f74e89b5a10d22c842db08cbf')");
      }

      @Override
      public void dropAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `videos`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `watch_progress`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `downloads`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `favorites`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `watch_later`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `watch_history`");
      }

      @Override
      public void onCreate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      public void onOpen(@NonNull final SQLiteConnection connection) {
        internalInitInvalidationTracker(connection);
      }

      @Override
      public void onPreMigrate(@NonNull final SQLiteConnection connection) {
        DBUtil.dropFtsSyncTriggers(connection);
      }

      @Override
      public void onPostMigrate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      @NonNull
      public RoomOpenDelegate.ValidationResult onValidateSchema(
          @NonNull final SQLiteConnection connection) {
        final Map<String, TableInfo.Column> _columnsVideos = new HashMap<String, TableInfo.Column>(13);
        _columnsVideos.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVideos.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVideos.put("thumbnail", new TableInfo.Column("thumbnail", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVideos.put("videoUrl", new TableInfo.Column("videoUrl", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVideos.put("telegramFileId", new TableInfo.Column("telegramFileId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVideos.put("duration", new TableInfo.Column("duration", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVideos.put("size", new TableInfo.Column("size", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVideos.put("localPath", new TableInfo.Column("localPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVideos.put("isDownloaded", new TableInfo.Column("isDownloaded", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVideos.put("downloadProgress", new TableInfo.Column("downloadProgress", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVideos.put("sourceLabel", new TableInfo.Column("sourceLabel", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVideos.put("isStreamable", new TableInfo.Column("isStreamable", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVideos.put("uploadDate", new TableInfo.Column("uploadDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysVideos = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesVideos = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoVideos = new TableInfo("videos", _columnsVideos, _foreignKeysVideos, _indicesVideos);
        final TableInfo _existingVideos = TableInfo.read(connection, "videos");
        if (!_infoVideos.equals(_existingVideos)) {
          return new RoomOpenDelegate.ValidationResult(false, "videos(com.trixsearch.hasikit.data.local.entities.VideoEntity).\n"
                  + " Expected:\n" + _infoVideos + "\n"
                  + " Found:\n" + _existingVideos);
        }
        final Map<String, TableInfo.Column> _columnsWatchProgress = new HashMap<String, TableInfo.Column>(4);
        _columnsWatchProgress.put("videoId", new TableInfo.Column("videoId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWatchProgress.put("lastPosition", new TableInfo.Column("lastPosition", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWatchProgress.put("duration", new TableInfo.Column("duration", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWatchProgress.put("lastWatchedAt", new TableInfo.Column("lastWatchedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysWatchProgress = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesWatchProgress = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWatchProgress = new TableInfo("watch_progress", _columnsWatchProgress, _foreignKeysWatchProgress, _indicesWatchProgress);
        final TableInfo _existingWatchProgress = TableInfo.read(connection, "watch_progress");
        if (!_infoWatchProgress.equals(_existingWatchProgress)) {
          return new RoomOpenDelegate.ValidationResult(false, "watch_progress(com.trixsearch.hasikit.data.local.entities.WatchProgressEntity).\n"
                  + " Expected:\n" + _infoWatchProgress + "\n"
                  + " Found:\n" + _existingWatchProgress);
        }
        final Map<String, TableInfo.Column> _columnsDownloads = new HashMap<String, TableInfo.Column>(6);
        _columnsDownloads.put("videoId", new TableInfo.Column("videoId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDownloads.put("state", new TableInfo.Column("state", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDownloads.put("progress", new TableInfo.Column("progress", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDownloads.put("localPath", new TableInfo.Column("localPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDownloads.put("errorCode", new TableInfo.Column("errorCode", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDownloads.put("downloadId", new TableInfo.Column("downloadId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysDownloads = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesDownloads = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDownloads = new TableInfo("downloads", _columnsDownloads, _foreignKeysDownloads, _indicesDownloads);
        final TableInfo _existingDownloads = TableInfo.read(connection, "downloads");
        if (!_infoDownloads.equals(_existingDownloads)) {
          return new RoomOpenDelegate.ValidationResult(false, "downloads(com.trixsearch.hasikit.data.local.entities.DownloadEntity).\n"
                  + " Expected:\n" + _infoDownloads + "\n"
                  + " Found:\n" + _existingDownloads);
        }
        final Map<String, TableInfo.Column> _columnsFavorites = new HashMap<String, TableInfo.Column>(5);
        _columnsFavorites.put("videoId", new TableInfo.Column("videoId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavorites.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavorites.put("thumbnail", new TableInfo.Column("thumbnail", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavorites.put("source", new TableInfo.Column("source", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavorites.put("addedAt", new TableInfo.Column("addedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysFavorites = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesFavorites = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFavorites = new TableInfo("favorites", _columnsFavorites, _foreignKeysFavorites, _indicesFavorites);
        final TableInfo _existingFavorites = TableInfo.read(connection, "favorites");
        if (!_infoFavorites.equals(_existingFavorites)) {
          return new RoomOpenDelegate.ValidationResult(false, "favorites(com.trixsearch.hasikit.data.local.entities.FavoriteEntity).\n"
                  + " Expected:\n" + _infoFavorites + "\n"
                  + " Found:\n" + _existingFavorites);
        }
        final Map<String, TableInfo.Column> _columnsWatchLater = new HashMap<String, TableInfo.Column>(5);
        _columnsWatchLater.put("videoId", new TableInfo.Column("videoId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWatchLater.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWatchLater.put("thumbnail", new TableInfo.Column("thumbnail", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWatchLater.put("source", new TableInfo.Column("source", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWatchLater.put("addedAt", new TableInfo.Column("addedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysWatchLater = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesWatchLater = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWatchLater = new TableInfo("watch_later", _columnsWatchLater, _foreignKeysWatchLater, _indicesWatchLater);
        final TableInfo _existingWatchLater = TableInfo.read(connection, "watch_later");
        if (!_infoWatchLater.equals(_existingWatchLater)) {
          return new RoomOpenDelegate.ValidationResult(false, "watch_later(com.trixsearch.hasikit.data.local.entities.WatchLaterEntity).\n"
                  + " Expected:\n" + _infoWatchLater + "\n"
                  + " Found:\n" + _existingWatchLater);
        }
        final Map<String, TableInfo.Column> _columnsWatchHistory = new HashMap<String, TableInfo.Column>(5);
        _columnsWatchHistory.put("videoId", new TableInfo.Column("videoId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWatchHistory.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWatchHistory.put("thumbnail", new TableInfo.Column("thumbnail", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWatchHistory.put("source", new TableInfo.Column("source", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWatchHistory.put("watchedAt", new TableInfo.Column("watchedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysWatchHistory = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesWatchHistory = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWatchHistory = new TableInfo("watch_history", _columnsWatchHistory, _foreignKeysWatchHistory, _indicesWatchHistory);
        final TableInfo _existingWatchHistory = TableInfo.read(connection, "watch_history");
        if (!_infoWatchHistory.equals(_existingWatchHistory)) {
          return new RoomOpenDelegate.ValidationResult(false, "watch_history(com.trixsearch.hasikit.data.local.entities.WatchHistoryEntity).\n"
                  + " Expected:\n" + _infoWatchHistory + "\n"
                  + " Found:\n" + _existingWatchHistory);
        }
        return new RoomOpenDelegate.ValidationResult(true, null);
      }
    };
    return _openDelegate;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final Map<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final Map<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "videos", "watch_progress", "downloads", "favorites", "watch_later", "watch_history");
  }

  @Override
  public void clearAllTables() {
    super.performClear(false, "videos", "watch_progress", "downloads", "favorites", "watch_later", "watch_history");
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final Map<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(VideoDao.class, VideoDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final Set<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public VideoDao videoDao() {
    if (_videoDao != null) {
      return _videoDao;
    } else {
      synchronized(this) {
        if(_videoDao == null) {
          _videoDao = new VideoDao_Impl(this);
        }
        return _videoDao;
      }
    }
  }
}
