package com.trixsearch.hasikit.data.local.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.coroutines.FlowUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.trixsearch.hasikit.data.local.entities.DownloadEntity;
import com.trixsearch.hasikit.data.local.entities.FavoriteEntity;
import com.trixsearch.hasikit.data.local.entities.VideoEntity;
import com.trixsearch.hasikit.data.local.entities.WatchHistoryEntity;
import com.trixsearch.hasikit.data.local.entities.WatchLaterEntity;
import com.trixsearch.hasikit.data.local.entities.WatchProgressEntity;
import java.lang.Class;
import java.lang.Integer;
import java.lang.Long;
import java.lang.NullPointerException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class VideoDao_Impl implements VideoDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<VideoEntity> __insertAdapterOfVideoEntity;

  private final EntityInsertAdapter<WatchProgressEntity> __insertAdapterOfWatchProgressEntity;

  private final EntityInsertAdapter<DownloadEntity> __insertAdapterOfDownloadEntity;

  private final EntityInsertAdapter<FavoriteEntity> __insertAdapterOfFavoriteEntity;

  private final EntityInsertAdapter<WatchLaterEntity> __insertAdapterOfWatchLaterEntity;

  private final EntityInsertAdapter<WatchHistoryEntity> __insertAdapterOfWatchHistoryEntity;

  private final EntityDeleteOrUpdateAdapter<VideoEntity> __deleteAdapterOfVideoEntity;

  private final EntityDeleteOrUpdateAdapter<VideoEntity> __updateAdapterOfVideoEntity;

  public VideoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfVideoEntity = new EntityInsertAdapter<VideoEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `videos` (`id`,`title`,`thumbnail`,`videoUrl`,`telegramFileId`,`duration`,`size`,`localPath`,`isDownloaded`,`downloadProgress`,`sourceLabel`,`isStreamable`,`uploadDate`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final VideoEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getId());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getTitle());
        }
        if (entity.getThumbnail() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getThumbnail());
        }
        if (entity.getVideoUrl() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getVideoUrl());
        }
        if (entity.getTelegramFileId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getTelegramFileId());
        }
        statement.bindLong(6, entity.getDuration());
        statement.bindLong(7, entity.getSize());
        if (entity.getLocalPath() == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, entity.getLocalPath());
        }
        final int _tmp = entity.isDownloaded() ? 1 : 0;
        statement.bindLong(9, _tmp);
        statement.bindDouble(10, entity.getDownloadProgress());
        if (entity.getSourceLabel() == null) {
          statement.bindNull(11);
        } else {
          statement.bindText(11, entity.getSourceLabel());
        }
        final int _tmp_1 = entity.isStreamable() ? 1 : 0;
        statement.bindLong(12, _tmp_1);
        statement.bindLong(13, entity.getUploadDate());
      }
    };
    this.__insertAdapterOfWatchProgressEntity = new EntityInsertAdapter<WatchProgressEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `watch_progress` (`videoId`,`lastPosition`,`duration`,`lastWatchedAt`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final WatchProgressEntity entity) {
        if (entity.getVideoId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getVideoId());
        }
        statement.bindLong(2, entity.getLastPosition());
        statement.bindLong(3, entity.getDuration());
        statement.bindLong(4, entity.getLastWatchedAt());
      }
    };
    this.__insertAdapterOfDownloadEntity = new EntityInsertAdapter<DownloadEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `downloads` (`videoId`,`state`,`progress`,`localPath`,`errorCode`,`downloadId`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final DownloadEntity entity) {
        if (entity.getVideoId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getVideoId());
        }
        if (entity.getState() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getState());
        }
        statement.bindDouble(3, entity.getProgress());
        if (entity.getLocalPath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getLocalPath());
        }
        if (entity.getErrorCode() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getErrorCode());
        }
        if (entity.getDownloadId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getDownloadId());
        }
      }
    };
    this.__insertAdapterOfFavoriteEntity = new EntityInsertAdapter<FavoriteEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `favorites` (`videoId`,`title`,`thumbnail`,`source`,`addedAt`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final FavoriteEntity entity) {
        if (entity.getVideoId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getVideoId());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getTitle());
        }
        if (entity.getThumbnail() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getThumbnail());
        }
        if (entity.getSource() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getSource());
        }
        statement.bindLong(5, entity.getAddedAt());
      }
    };
    this.__insertAdapterOfWatchLaterEntity = new EntityInsertAdapter<WatchLaterEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `watch_later` (`videoId`,`title`,`thumbnail`,`source`,`addedAt`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final WatchLaterEntity entity) {
        if (entity.getVideoId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getVideoId());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getTitle());
        }
        if (entity.getThumbnail() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getThumbnail());
        }
        if (entity.getSource() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getSource());
        }
        statement.bindLong(5, entity.getAddedAt());
      }
    };
    this.__insertAdapterOfWatchHistoryEntity = new EntityInsertAdapter<WatchHistoryEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `watch_history` (`videoId`,`title`,`thumbnail`,`source`,`watchedAt`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final WatchHistoryEntity entity) {
        if (entity.getVideoId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getVideoId());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getTitle());
        }
        if (entity.getThumbnail() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getThumbnail());
        }
        if (entity.getSource() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getSource());
        }
        statement.bindLong(5, entity.getWatchedAt());
      }
    };
    this.__deleteAdapterOfVideoEntity = new EntityDeleteOrUpdateAdapter<VideoEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `videos` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final VideoEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getId());
        }
      }
    };
    this.__updateAdapterOfVideoEntity = new EntityDeleteOrUpdateAdapter<VideoEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `videos` SET `id` = ?,`title` = ?,`thumbnail` = ?,`videoUrl` = ?,`telegramFileId` = ?,`duration` = ?,`size` = ?,`localPath` = ?,`isDownloaded` = ?,`downloadProgress` = ?,`sourceLabel` = ?,`isStreamable` = ?,`uploadDate` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final VideoEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getId());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getTitle());
        }
        if (entity.getThumbnail() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getThumbnail());
        }
        if (entity.getVideoUrl() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getVideoUrl());
        }
        if (entity.getTelegramFileId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getTelegramFileId());
        }
        statement.bindLong(6, entity.getDuration());
        statement.bindLong(7, entity.getSize());
        if (entity.getLocalPath() == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, entity.getLocalPath());
        }
        final int _tmp = entity.isDownloaded() ? 1 : 0;
        statement.bindLong(9, _tmp);
        statement.bindDouble(10, entity.getDownloadProgress());
        if (entity.getSourceLabel() == null) {
          statement.bindNull(11);
        } else {
          statement.bindText(11, entity.getSourceLabel());
        }
        final int _tmp_1 = entity.isStreamable() ? 1 : 0;
        statement.bindLong(12, _tmp_1);
        statement.bindLong(13, entity.getUploadDate());
        if (entity.getId() == null) {
          statement.bindNull(14);
        } else {
          statement.bindText(14, entity.getId());
        }
      }
    };
  }

  @Override
  public Object insertVideo(final VideoEntity video, final Continuation<? super Unit> $completion) {
    if (video == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfVideoEntity.insert(_connection, video);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object saveWatchProgress(final WatchProgressEntity progress,
      final Continuation<? super Unit> $completion) {
    if (progress == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfWatchProgressEntity.insert(_connection, progress);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object saveDownload(final DownloadEntity download,
      final Continuation<? super Unit> $completion) {
    if (download == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfDownloadEntity.insert(_connection, download);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object addFavorite(final FavoriteEntity favorite,
      final Continuation<? super Unit> $completion) {
    if (favorite == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfFavoriteEntity.insert(_connection, favorite);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object addToWatchLater(final WatchLaterEntity item,
      final Continuation<? super Unit> $completion) {
    if (item == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfWatchLaterEntity.insert(_connection, item);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object addToWatchHistory(final WatchHistoryEntity item,
      final Continuation<? super Unit> $completion) {
    if (item == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfWatchHistoryEntity.insert(_connection, item);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object deleteVideo(final VideoEntity video, final Continuation<? super Unit> $completion) {
    if (video == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __deleteAdapterOfVideoEntity.handle(_connection, video);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object updateVideo(final VideoEntity video, final Continuation<? super Unit> $completion) {
    if (video == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfVideoEntity.handle(_connection, video);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Flow<List<VideoEntity>> getAllVideos() {
    final String _sql = "SELECT * FROM videos ORDER BY uploadDate DESC, id DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"videos"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfThumbnail = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "thumbnail");
        final int _columnIndexOfVideoUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoUrl");
        final int _columnIndexOfTelegramFileId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "telegramFileId");
        final int _columnIndexOfDuration = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "duration");
        final int _columnIndexOfSize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "size");
        final int _columnIndexOfLocalPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localPath");
        final int _columnIndexOfIsDownloaded = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isDownloaded");
        final int _columnIndexOfDownloadProgress = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "downloadProgress");
        final int _columnIndexOfSourceLabel = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sourceLabel");
        final int _columnIndexOfIsStreamable = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isStreamable");
        final int _columnIndexOfUploadDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uploadDate");
        final List<VideoEntity> _result = new ArrayList<VideoEntity>();
        while (_stmt.step()) {
          final VideoEntity _item;
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpThumbnail;
          if (_stmt.isNull(_columnIndexOfThumbnail)) {
            _tmpThumbnail = null;
          } else {
            _tmpThumbnail = _stmt.getText(_columnIndexOfThumbnail);
          }
          final String _tmpVideoUrl;
          if (_stmt.isNull(_columnIndexOfVideoUrl)) {
            _tmpVideoUrl = null;
          } else {
            _tmpVideoUrl = _stmt.getText(_columnIndexOfVideoUrl);
          }
          final String _tmpTelegramFileId;
          if (_stmt.isNull(_columnIndexOfTelegramFileId)) {
            _tmpTelegramFileId = null;
          } else {
            _tmpTelegramFileId = _stmt.getText(_columnIndexOfTelegramFileId);
          }
          final long _tmpDuration;
          _tmpDuration = _stmt.getLong(_columnIndexOfDuration);
          final long _tmpSize;
          _tmpSize = _stmt.getLong(_columnIndexOfSize);
          final String _tmpLocalPath;
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null;
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath);
          }
          final boolean _tmpIsDownloaded;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsDownloaded));
          _tmpIsDownloaded = _tmp != 0;
          final float _tmpDownloadProgress;
          _tmpDownloadProgress = (float) (_stmt.getDouble(_columnIndexOfDownloadProgress));
          final String _tmpSourceLabel;
          if (_stmt.isNull(_columnIndexOfSourceLabel)) {
            _tmpSourceLabel = null;
          } else {
            _tmpSourceLabel = _stmt.getText(_columnIndexOfSourceLabel);
          }
          final boolean _tmpIsStreamable;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfIsStreamable));
          _tmpIsStreamable = _tmp_1 != 0;
          final int _tmpUploadDate;
          _tmpUploadDate = (int) (_stmt.getLong(_columnIndexOfUploadDate));
          _item = new VideoEntity(_tmpId,_tmpTitle,_tmpThumbnail,_tmpVideoUrl,_tmpTelegramFileId,_tmpDuration,_tmpSize,_tmpLocalPath,_tmpIsDownloaded,_tmpDownloadProgress,_tmpSourceLabel,_tmpIsStreamable,_tmpUploadDate);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object getVideoById(final String id, final Continuation<? super VideoEntity> $completion) {
    final String _sql = "SELECT * FROM videos WHERE id = ? LIMIT 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (id == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, id);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfThumbnail = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "thumbnail");
        final int _columnIndexOfVideoUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoUrl");
        final int _columnIndexOfTelegramFileId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "telegramFileId");
        final int _columnIndexOfDuration = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "duration");
        final int _columnIndexOfSize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "size");
        final int _columnIndexOfLocalPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localPath");
        final int _columnIndexOfIsDownloaded = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isDownloaded");
        final int _columnIndexOfDownloadProgress = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "downloadProgress");
        final int _columnIndexOfSourceLabel = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sourceLabel");
        final int _columnIndexOfIsStreamable = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isStreamable");
        final int _columnIndexOfUploadDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uploadDate");
        final VideoEntity _result;
        if (_stmt.step()) {
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpThumbnail;
          if (_stmt.isNull(_columnIndexOfThumbnail)) {
            _tmpThumbnail = null;
          } else {
            _tmpThumbnail = _stmt.getText(_columnIndexOfThumbnail);
          }
          final String _tmpVideoUrl;
          if (_stmt.isNull(_columnIndexOfVideoUrl)) {
            _tmpVideoUrl = null;
          } else {
            _tmpVideoUrl = _stmt.getText(_columnIndexOfVideoUrl);
          }
          final String _tmpTelegramFileId;
          if (_stmt.isNull(_columnIndexOfTelegramFileId)) {
            _tmpTelegramFileId = null;
          } else {
            _tmpTelegramFileId = _stmt.getText(_columnIndexOfTelegramFileId);
          }
          final long _tmpDuration;
          _tmpDuration = _stmt.getLong(_columnIndexOfDuration);
          final long _tmpSize;
          _tmpSize = _stmt.getLong(_columnIndexOfSize);
          final String _tmpLocalPath;
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null;
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath);
          }
          final boolean _tmpIsDownloaded;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsDownloaded));
          _tmpIsDownloaded = _tmp != 0;
          final float _tmpDownloadProgress;
          _tmpDownloadProgress = (float) (_stmt.getDouble(_columnIndexOfDownloadProgress));
          final String _tmpSourceLabel;
          if (_stmt.isNull(_columnIndexOfSourceLabel)) {
            _tmpSourceLabel = null;
          } else {
            _tmpSourceLabel = _stmt.getText(_columnIndexOfSourceLabel);
          }
          final boolean _tmpIsStreamable;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfIsStreamable));
          _tmpIsStreamable = _tmp_1 != 0;
          final int _tmpUploadDate;
          _tmpUploadDate = (int) (_stmt.getLong(_columnIndexOfUploadDate));
          _result = new VideoEntity(_tmpId,_tmpTitle,_tmpThumbnail,_tmpVideoUrl,_tmpTelegramFileId,_tmpDuration,_tmpSize,_tmpLocalPath,_tmpIsDownloaded,_tmpDownloadProgress,_tmpSourceLabel,_tmpIsStreamable,_tmpUploadDate);
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Flow<List<VideoEntity>> getDownloadedVideos() {
    final String _sql = "SELECT * FROM videos WHERE isDownloaded = 1 ORDER BY title ASC";
    return FlowUtil.createFlow(__db, false, new String[] {"videos"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfThumbnail = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "thumbnail");
        final int _columnIndexOfVideoUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoUrl");
        final int _columnIndexOfTelegramFileId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "telegramFileId");
        final int _columnIndexOfDuration = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "duration");
        final int _columnIndexOfSize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "size");
        final int _columnIndexOfLocalPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localPath");
        final int _columnIndexOfIsDownloaded = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isDownloaded");
        final int _columnIndexOfDownloadProgress = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "downloadProgress");
        final int _columnIndexOfSourceLabel = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sourceLabel");
        final int _columnIndexOfIsStreamable = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isStreamable");
        final int _columnIndexOfUploadDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uploadDate");
        final List<VideoEntity> _result = new ArrayList<VideoEntity>();
        while (_stmt.step()) {
          final VideoEntity _item;
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpThumbnail;
          if (_stmt.isNull(_columnIndexOfThumbnail)) {
            _tmpThumbnail = null;
          } else {
            _tmpThumbnail = _stmt.getText(_columnIndexOfThumbnail);
          }
          final String _tmpVideoUrl;
          if (_stmt.isNull(_columnIndexOfVideoUrl)) {
            _tmpVideoUrl = null;
          } else {
            _tmpVideoUrl = _stmt.getText(_columnIndexOfVideoUrl);
          }
          final String _tmpTelegramFileId;
          if (_stmt.isNull(_columnIndexOfTelegramFileId)) {
            _tmpTelegramFileId = null;
          } else {
            _tmpTelegramFileId = _stmt.getText(_columnIndexOfTelegramFileId);
          }
          final long _tmpDuration;
          _tmpDuration = _stmt.getLong(_columnIndexOfDuration);
          final long _tmpSize;
          _tmpSize = _stmt.getLong(_columnIndexOfSize);
          final String _tmpLocalPath;
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null;
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath);
          }
          final boolean _tmpIsDownloaded;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsDownloaded));
          _tmpIsDownloaded = _tmp != 0;
          final float _tmpDownloadProgress;
          _tmpDownloadProgress = (float) (_stmt.getDouble(_columnIndexOfDownloadProgress));
          final String _tmpSourceLabel;
          if (_stmt.isNull(_columnIndexOfSourceLabel)) {
            _tmpSourceLabel = null;
          } else {
            _tmpSourceLabel = _stmt.getText(_columnIndexOfSourceLabel);
          }
          final boolean _tmpIsStreamable;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfIsStreamable));
          _tmpIsStreamable = _tmp_1 != 0;
          final int _tmpUploadDate;
          _tmpUploadDate = (int) (_stmt.getLong(_columnIndexOfUploadDate));
          _item = new VideoEntity(_tmpId,_tmpTitle,_tmpThumbnail,_tmpVideoUrl,_tmpTelegramFileId,_tmpDuration,_tmpSize,_tmpLocalPath,_tmpIsDownloaded,_tmpDownloadProgress,_tmpSourceLabel,_tmpIsStreamable,_tmpUploadDate);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<VideoEntity>> downloadedByNameAsc() {
    final String _sql = "SELECT * FROM videos WHERE isDownloaded = 1 ORDER BY title ASC";
    return FlowUtil.createFlow(__db, false, new String[] {"videos"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfThumbnail = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "thumbnail");
        final int _columnIndexOfVideoUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoUrl");
        final int _columnIndexOfTelegramFileId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "telegramFileId");
        final int _columnIndexOfDuration = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "duration");
        final int _columnIndexOfSize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "size");
        final int _columnIndexOfLocalPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localPath");
        final int _columnIndexOfIsDownloaded = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isDownloaded");
        final int _columnIndexOfDownloadProgress = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "downloadProgress");
        final int _columnIndexOfSourceLabel = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sourceLabel");
        final int _columnIndexOfIsStreamable = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isStreamable");
        final int _columnIndexOfUploadDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uploadDate");
        final List<VideoEntity> _result = new ArrayList<VideoEntity>();
        while (_stmt.step()) {
          final VideoEntity _item;
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpThumbnail;
          if (_stmt.isNull(_columnIndexOfThumbnail)) {
            _tmpThumbnail = null;
          } else {
            _tmpThumbnail = _stmt.getText(_columnIndexOfThumbnail);
          }
          final String _tmpVideoUrl;
          if (_stmt.isNull(_columnIndexOfVideoUrl)) {
            _tmpVideoUrl = null;
          } else {
            _tmpVideoUrl = _stmt.getText(_columnIndexOfVideoUrl);
          }
          final String _tmpTelegramFileId;
          if (_stmt.isNull(_columnIndexOfTelegramFileId)) {
            _tmpTelegramFileId = null;
          } else {
            _tmpTelegramFileId = _stmt.getText(_columnIndexOfTelegramFileId);
          }
          final long _tmpDuration;
          _tmpDuration = _stmt.getLong(_columnIndexOfDuration);
          final long _tmpSize;
          _tmpSize = _stmt.getLong(_columnIndexOfSize);
          final String _tmpLocalPath;
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null;
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath);
          }
          final boolean _tmpIsDownloaded;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsDownloaded));
          _tmpIsDownloaded = _tmp != 0;
          final float _tmpDownloadProgress;
          _tmpDownloadProgress = (float) (_stmt.getDouble(_columnIndexOfDownloadProgress));
          final String _tmpSourceLabel;
          if (_stmt.isNull(_columnIndexOfSourceLabel)) {
            _tmpSourceLabel = null;
          } else {
            _tmpSourceLabel = _stmt.getText(_columnIndexOfSourceLabel);
          }
          final boolean _tmpIsStreamable;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfIsStreamable));
          _tmpIsStreamable = _tmp_1 != 0;
          final int _tmpUploadDate;
          _tmpUploadDate = (int) (_stmt.getLong(_columnIndexOfUploadDate));
          _item = new VideoEntity(_tmpId,_tmpTitle,_tmpThumbnail,_tmpVideoUrl,_tmpTelegramFileId,_tmpDuration,_tmpSize,_tmpLocalPath,_tmpIsDownloaded,_tmpDownloadProgress,_tmpSourceLabel,_tmpIsStreamable,_tmpUploadDate);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<VideoEntity>> downloadedByDateDesc() {
    final String _sql = "SELECT * FROM videos WHERE isDownloaded = 1 ORDER BY uploadDate DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"videos"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfThumbnail = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "thumbnail");
        final int _columnIndexOfVideoUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoUrl");
        final int _columnIndexOfTelegramFileId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "telegramFileId");
        final int _columnIndexOfDuration = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "duration");
        final int _columnIndexOfSize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "size");
        final int _columnIndexOfLocalPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localPath");
        final int _columnIndexOfIsDownloaded = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isDownloaded");
        final int _columnIndexOfDownloadProgress = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "downloadProgress");
        final int _columnIndexOfSourceLabel = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sourceLabel");
        final int _columnIndexOfIsStreamable = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isStreamable");
        final int _columnIndexOfUploadDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uploadDate");
        final List<VideoEntity> _result = new ArrayList<VideoEntity>();
        while (_stmt.step()) {
          final VideoEntity _item;
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpThumbnail;
          if (_stmt.isNull(_columnIndexOfThumbnail)) {
            _tmpThumbnail = null;
          } else {
            _tmpThumbnail = _stmt.getText(_columnIndexOfThumbnail);
          }
          final String _tmpVideoUrl;
          if (_stmt.isNull(_columnIndexOfVideoUrl)) {
            _tmpVideoUrl = null;
          } else {
            _tmpVideoUrl = _stmt.getText(_columnIndexOfVideoUrl);
          }
          final String _tmpTelegramFileId;
          if (_stmt.isNull(_columnIndexOfTelegramFileId)) {
            _tmpTelegramFileId = null;
          } else {
            _tmpTelegramFileId = _stmt.getText(_columnIndexOfTelegramFileId);
          }
          final long _tmpDuration;
          _tmpDuration = _stmt.getLong(_columnIndexOfDuration);
          final long _tmpSize;
          _tmpSize = _stmt.getLong(_columnIndexOfSize);
          final String _tmpLocalPath;
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null;
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath);
          }
          final boolean _tmpIsDownloaded;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsDownloaded));
          _tmpIsDownloaded = _tmp != 0;
          final float _tmpDownloadProgress;
          _tmpDownloadProgress = (float) (_stmt.getDouble(_columnIndexOfDownloadProgress));
          final String _tmpSourceLabel;
          if (_stmt.isNull(_columnIndexOfSourceLabel)) {
            _tmpSourceLabel = null;
          } else {
            _tmpSourceLabel = _stmt.getText(_columnIndexOfSourceLabel);
          }
          final boolean _tmpIsStreamable;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfIsStreamable));
          _tmpIsStreamable = _tmp_1 != 0;
          final int _tmpUploadDate;
          _tmpUploadDate = (int) (_stmt.getLong(_columnIndexOfUploadDate));
          _item = new VideoEntity(_tmpId,_tmpTitle,_tmpThumbnail,_tmpVideoUrl,_tmpTelegramFileId,_tmpDuration,_tmpSize,_tmpLocalPath,_tmpIsDownloaded,_tmpDownloadProgress,_tmpSourceLabel,_tmpIsStreamable,_tmpUploadDate);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<VideoEntity>> downloadedBySizeDesc() {
    final String _sql = "SELECT * FROM videos WHERE isDownloaded = 1 ORDER BY size DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"videos"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfThumbnail = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "thumbnail");
        final int _columnIndexOfVideoUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoUrl");
        final int _columnIndexOfTelegramFileId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "telegramFileId");
        final int _columnIndexOfDuration = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "duration");
        final int _columnIndexOfSize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "size");
        final int _columnIndexOfLocalPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localPath");
        final int _columnIndexOfIsDownloaded = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isDownloaded");
        final int _columnIndexOfDownloadProgress = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "downloadProgress");
        final int _columnIndexOfSourceLabel = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sourceLabel");
        final int _columnIndexOfIsStreamable = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isStreamable");
        final int _columnIndexOfUploadDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uploadDate");
        final List<VideoEntity> _result = new ArrayList<VideoEntity>();
        while (_stmt.step()) {
          final VideoEntity _item;
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpThumbnail;
          if (_stmt.isNull(_columnIndexOfThumbnail)) {
            _tmpThumbnail = null;
          } else {
            _tmpThumbnail = _stmt.getText(_columnIndexOfThumbnail);
          }
          final String _tmpVideoUrl;
          if (_stmt.isNull(_columnIndexOfVideoUrl)) {
            _tmpVideoUrl = null;
          } else {
            _tmpVideoUrl = _stmt.getText(_columnIndexOfVideoUrl);
          }
          final String _tmpTelegramFileId;
          if (_stmt.isNull(_columnIndexOfTelegramFileId)) {
            _tmpTelegramFileId = null;
          } else {
            _tmpTelegramFileId = _stmt.getText(_columnIndexOfTelegramFileId);
          }
          final long _tmpDuration;
          _tmpDuration = _stmt.getLong(_columnIndexOfDuration);
          final long _tmpSize;
          _tmpSize = _stmt.getLong(_columnIndexOfSize);
          final String _tmpLocalPath;
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null;
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath);
          }
          final boolean _tmpIsDownloaded;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsDownloaded));
          _tmpIsDownloaded = _tmp != 0;
          final float _tmpDownloadProgress;
          _tmpDownloadProgress = (float) (_stmt.getDouble(_columnIndexOfDownloadProgress));
          final String _tmpSourceLabel;
          if (_stmt.isNull(_columnIndexOfSourceLabel)) {
            _tmpSourceLabel = null;
          } else {
            _tmpSourceLabel = _stmt.getText(_columnIndexOfSourceLabel);
          }
          final boolean _tmpIsStreamable;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfIsStreamable));
          _tmpIsStreamable = _tmp_1 != 0;
          final int _tmpUploadDate;
          _tmpUploadDate = (int) (_stmt.getLong(_columnIndexOfUploadDate));
          _item = new VideoEntity(_tmpId,_tmpTitle,_tmpThumbnail,_tmpVideoUrl,_tmpTelegramFileId,_tmpDuration,_tmpSize,_tmpLocalPath,_tmpIsDownloaded,_tmpDownloadProgress,_tmpSourceLabel,_tmpIsStreamable,_tmpUploadDate);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<VideoEntity>> downloadedByDurationDesc() {
    final String _sql = "SELECT * FROM videos WHERE isDownloaded = 1 ORDER BY duration DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"videos"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfThumbnail = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "thumbnail");
        final int _columnIndexOfVideoUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoUrl");
        final int _columnIndexOfTelegramFileId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "telegramFileId");
        final int _columnIndexOfDuration = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "duration");
        final int _columnIndexOfSize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "size");
        final int _columnIndexOfLocalPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localPath");
        final int _columnIndexOfIsDownloaded = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isDownloaded");
        final int _columnIndexOfDownloadProgress = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "downloadProgress");
        final int _columnIndexOfSourceLabel = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sourceLabel");
        final int _columnIndexOfIsStreamable = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isStreamable");
        final int _columnIndexOfUploadDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uploadDate");
        final List<VideoEntity> _result = new ArrayList<VideoEntity>();
        while (_stmt.step()) {
          final VideoEntity _item;
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpThumbnail;
          if (_stmt.isNull(_columnIndexOfThumbnail)) {
            _tmpThumbnail = null;
          } else {
            _tmpThumbnail = _stmt.getText(_columnIndexOfThumbnail);
          }
          final String _tmpVideoUrl;
          if (_stmt.isNull(_columnIndexOfVideoUrl)) {
            _tmpVideoUrl = null;
          } else {
            _tmpVideoUrl = _stmt.getText(_columnIndexOfVideoUrl);
          }
          final String _tmpTelegramFileId;
          if (_stmt.isNull(_columnIndexOfTelegramFileId)) {
            _tmpTelegramFileId = null;
          } else {
            _tmpTelegramFileId = _stmt.getText(_columnIndexOfTelegramFileId);
          }
          final long _tmpDuration;
          _tmpDuration = _stmt.getLong(_columnIndexOfDuration);
          final long _tmpSize;
          _tmpSize = _stmt.getLong(_columnIndexOfSize);
          final String _tmpLocalPath;
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null;
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath);
          }
          final boolean _tmpIsDownloaded;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsDownloaded));
          _tmpIsDownloaded = _tmp != 0;
          final float _tmpDownloadProgress;
          _tmpDownloadProgress = (float) (_stmt.getDouble(_columnIndexOfDownloadProgress));
          final String _tmpSourceLabel;
          if (_stmt.isNull(_columnIndexOfSourceLabel)) {
            _tmpSourceLabel = null;
          } else {
            _tmpSourceLabel = _stmt.getText(_columnIndexOfSourceLabel);
          }
          final boolean _tmpIsStreamable;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfIsStreamable));
          _tmpIsStreamable = _tmp_1 != 0;
          final int _tmpUploadDate;
          _tmpUploadDate = (int) (_stmt.getLong(_columnIndexOfUploadDate));
          _item = new VideoEntity(_tmpId,_tmpTitle,_tmpThumbnail,_tmpVideoUrl,_tmpTelegramFileId,_tmpDuration,_tmpSize,_tmpLocalPath,_tmpIsDownloaded,_tmpDownloadProgress,_tmpSourceLabel,_tmpIsStreamable,_tmpUploadDate);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<VideoEntity>> downloadedByChannel() {
    final String _sql = "SELECT * FROM videos WHERE isDownloaded = 1 ORDER BY sourceLabel ASC, title ASC";
    return FlowUtil.createFlow(__db, false, new String[] {"videos"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfThumbnail = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "thumbnail");
        final int _columnIndexOfVideoUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoUrl");
        final int _columnIndexOfTelegramFileId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "telegramFileId");
        final int _columnIndexOfDuration = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "duration");
        final int _columnIndexOfSize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "size");
        final int _columnIndexOfLocalPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localPath");
        final int _columnIndexOfIsDownloaded = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isDownloaded");
        final int _columnIndexOfDownloadProgress = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "downloadProgress");
        final int _columnIndexOfSourceLabel = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sourceLabel");
        final int _columnIndexOfIsStreamable = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isStreamable");
        final int _columnIndexOfUploadDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uploadDate");
        final List<VideoEntity> _result = new ArrayList<VideoEntity>();
        while (_stmt.step()) {
          final VideoEntity _item;
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpThumbnail;
          if (_stmt.isNull(_columnIndexOfThumbnail)) {
            _tmpThumbnail = null;
          } else {
            _tmpThumbnail = _stmt.getText(_columnIndexOfThumbnail);
          }
          final String _tmpVideoUrl;
          if (_stmt.isNull(_columnIndexOfVideoUrl)) {
            _tmpVideoUrl = null;
          } else {
            _tmpVideoUrl = _stmt.getText(_columnIndexOfVideoUrl);
          }
          final String _tmpTelegramFileId;
          if (_stmt.isNull(_columnIndexOfTelegramFileId)) {
            _tmpTelegramFileId = null;
          } else {
            _tmpTelegramFileId = _stmt.getText(_columnIndexOfTelegramFileId);
          }
          final long _tmpDuration;
          _tmpDuration = _stmt.getLong(_columnIndexOfDuration);
          final long _tmpSize;
          _tmpSize = _stmt.getLong(_columnIndexOfSize);
          final String _tmpLocalPath;
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null;
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath);
          }
          final boolean _tmpIsDownloaded;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsDownloaded));
          _tmpIsDownloaded = _tmp != 0;
          final float _tmpDownloadProgress;
          _tmpDownloadProgress = (float) (_stmt.getDouble(_columnIndexOfDownloadProgress));
          final String _tmpSourceLabel;
          if (_stmt.isNull(_columnIndexOfSourceLabel)) {
            _tmpSourceLabel = null;
          } else {
            _tmpSourceLabel = _stmt.getText(_columnIndexOfSourceLabel);
          }
          final boolean _tmpIsStreamable;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfIsStreamable));
          _tmpIsStreamable = _tmp_1 != 0;
          final int _tmpUploadDate;
          _tmpUploadDate = (int) (_stmt.getLong(_columnIndexOfUploadDate));
          _item = new VideoEntity(_tmpId,_tmpTitle,_tmpThumbnail,_tmpVideoUrl,_tmpTelegramFileId,_tmpDuration,_tmpSize,_tmpLocalPath,_tmpIsDownloaded,_tmpDownloadProgress,_tmpSourceLabel,_tmpIsStreamable,_tmpUploadDate);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<VideoEntity>> searchVideos(final String query) {
    final String _sql = "\n"
            + "        SELECT * FROM videos\n"
            + "        WHERE title LIKE '%' || ? || '%'\n"
            + "           OR sourceLabel LIKE '%' || ? || '%'\n"
            + "        ORDER BY uploadDate DESC\n"
            + "    ";
    return FlowUtil.createFlow(__db, false, new String[] {"videos"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (query == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, query);
        }
        _argIndex = 2;
        if (query == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, query);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfThumbnail = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "thumbnail");
        final int _columnIndexOfVideoUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoUrl");
        final int _columnIndexOfTelegramFileId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "telegramFileId");
        final int _columnIndexOfDuration = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "duration");
        final int _columnIndexOfSize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "size");
        final int _columnIndexOfLocalPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localPath");
        final int _columnIndexOfIsDownloaded = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isDownloaded");
        final int _columnIndexOfDownloadProgress = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "downloadProgress");
        final int _columnIndexOfSourceLabel = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sourceLabel");
        final int _columnIndexOfIsStreamable = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isStreamable");
        final int _columnIndexOfUploadDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uploadDate");
        final List<VideoEntity> _result = new ArrayList<VideoEntity>();
        while (_stmt.step()) {
          final VideoEntity _item;
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpThumbnail;
          if (_stmt.isNull(_columnIndexOfThumbnail)) {
            _tmpThumbnail = null;
          } else {
            _tmpThumbnail = _stmt.getText(_columnIndexOfThumbnail);
          }
          final String _tmpVideoUrl;
          if (_stmt.isNull(_columnIndexOfVideoUrl)) {
            _tmpVideoUrl = null;
          } else {
            _tmpVideoUrl = _stmt.getText(_columnIndexOfVideoUrl);
          }
          final String _tmpTelegramFileId;
          if (_stmt.isNull(_columnIndexOfTelegramFileId)) {
            _tmpTelegramFileId = null;
          } else {
            _tmpTelegramFileId = _stmt.getText(_columnIndexOfTelegramFileId);
          }
          final long _tmpDuration;
          _tmpDuration = _stmt.getLong(_columnIndexOfDuration);
          final long _tmpSize;
          _tmpSize = _stmt.getLong(_columnIndexOfSize);
          final String _tmpLocalPath;
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null;
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath);
          }
          final boolean _tmpIsDownloaded;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsDownloaded));
          _tmpIsDownloaded = _tmp != 0;
          final float _tmpDownloadProgress;
          _tmpDownloadProgress = (float) (_stmt.getDouble(_columnIndexOfDownloadProgress));
          final String _tmpSourceLabel;
          if (_stmt.isNull(_columnIndexOfSourceLabel)) {
            _tmpSourceLabel = null;
          } else {
            _tmpSourceLabel = _stmt.getText(_columnIndexOfSourceLabel);
          }
          final boolean _tmpIsStreamable;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfIsStreamable));
          _tmpIsStreamable = _tmp_1 != 0;
          final int _tmpUploadDate;
          _tmpUploadDate = (int) (_stmt.getLong(_columnIndexOfUploadDate));
          _item = new VideoEntity(_tmpId,_tmpTitle,_tmpThumbnail,_tmpVideoUrl,_tmpTelegramFileId,_tmpDuration,_tmpSize,_tmpLocalPath,_tmpIsDownloaded,_tmpDownloadProgress,_tmpSourceLabel,_tmpIsStreamable,_tmpUploadDate);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<VideoEntity>> searchDownloadedVideos(final String query) {
    final String _sql = "\n"
            + "        SELECT * FROM videos\n"
            + "        WHERE isDownloaded = 1\n"
            + "          AND (title LIKE '%' || ? || '%' OR sourceLabel LIKE '%' || ? || '%')\n"
            + "        ORDER BY title ASC\n"
            + "    ";
    return FlowUtil.createFlow(__db, false, new String[] {"videos"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (query == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, query);
        }
        _argIndex = 2;
        if (query == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, query);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfThumbnail = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "thumbnail");
        final int _columnIndexOfVideoUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoUrl");
        final int _columnIndexOfTelegramFileId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "telegramFileId");
        final int _columnIndexOfDuration = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "duration");
        final int _columnIndexOfSize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "size");
        final int _columnIndexOfLocalPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localPath");
        final int _columnIndexOfIsDownloaded = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isDownloaded");
        final int _columnIndexOfDownloadProgress = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "downloadProgress");
        final int _columnIndexOfSourceLabel = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sourceLabel");
        final int _columnIndexOfIsStreamable = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isStreamable");
        final int _columnIndexOfUploadDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uploadDate");
        final List<VideoEntity> _result = new ArrayList<VideoEntity>();
        while (_stmt.step()) {
          final VideoEntity _item;
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpThumbnail;
          if (_stmt.isNull(_columnIndexOfThumbnail)) {
            _tmpThumbnail = null;
          } else {
            _tmpThumbnail = _stmt.getText(_columnIndexOfThumbnail);
          }
          final String _tmpVideoUrl;
          if (_stmt.isNull(_columnIndexOfVideoUrl)) {
            _tmpVideoUrl = null;
          } else {
            _tmpVideoUrl = _stmt.getText(_columnIndexOfVideoUrl);
          }
          final String _tmpTelegramFileId;
          if (_stmt.isNull(_columnIndexOfTelegramFileId)) {
            _tmpTelegramFileId = null;
          } else {
            _tmpTelegramFileId = _stmt.getText(_columnIndexOfTelegramFileId);
          }
          final long _tmpDuration;
          _tmpDuration = _stmt.getLong(_columnIndexOfDuration);
          final long _tmpSize;
          _tmpSize = _stmt.getLong(_columnIndexOfSize);
          final String _tmpLocalPath;
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null;
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath);
          }
          final boolean _tmpIsDownloaded;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsDownloaded));
          _tmpIsDownloaded = _tmp != 0;
          final float _tmpDownloadProgress;
          _tmpDownloadProgress = (float) (_stmt.getDouble(_columnIndexOfDownloadProgress));
          final String _tmpSourceLabel;
          if (_stmt.isNull(_columnIndexOfSourceLabel)) {
            _tmpSourceLabel = null;
          } else {
            _tmpSourceLabel = _stmt.getText(_columnIndexOfSourceLabel);
          }
          final boolean _tmpIsStreamable;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfIsStreamable));
          _tmpIsStreamable = _tmp_1 != 0;
          final int _tmpUploadDate;
          _tmpUploadDate = (int) (_stmt.getLong(_columnIndexOfUploadDate));
          _item = new VideoEntity(_tmpId,_tmpTitle,_tmpThumbnail,_tmpVideoUrl,_tmpTelegramFileId,_tmpDuration,_tmpSize,_tmpLocalPath,_tmpIsDownloaded,_tmpDownloadProgress,_tmpSourceLabel,_tmpIsStreamable,_tmpUploadDate);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<VideoEntity>> getVideosWithProgress() {
    final String _sql = "\n"
            + "        SELECT v.* FROM videos v\n"
            + "        INNER JOIN watch_progress wp ON v.id = wp.videoId\n"
            + "        WHERE wp.lastPosition > 0\n"
            + "        ORDER BY wp.lastWatchedAt DESC\n"
            + "        LIMIT 10\n"
            + "    ";
    return FlowUtil.createFlow(__db, false, new String[] {"videos",
        "watch_progress"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfThumbnail = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "thumbnail");
        final int _columnIndexOfVideoUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoUrl");
        final int _columnIndexOfTelegramFileId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "telegramFileId");
        final int _columnIndexOfDuration = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "duration");
        final int _columnIndexOfSize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "size");
        final int _columnIndexOfLocalPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localPath");
        final int _columnIndexOfIsDownloaded = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isDownloaded");
        final int _columnIndexOfDownloadProgress = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "downloadProgress");
        final int _columnIndexOfSourceLabel = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sourceLabel");
        final int _columnIndexOfIsStreamable = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isStreamable");
        final int _columnIndexOfUploadDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uploadDate");
        final List<VideoEntity> _result = new ArrayList<VideoEntity>();
        while (_stmt.step()) {
          final VideoEntity _item;
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpThumbnail;
          if (_stmt.isNull(_columnIndexOfThumbnail)) {
            _tmpThumbnail = null;
          } else {
            _tmpThumbnail = _stmt.getText(_columnIndexOfThumbnail);
          }
          final String _tmpVideoUrl;
          if (_stmt.isNull(_columnIndexOfVideoUrl)) {
            _tmpVideoUrl = null;
          } else {
            _tmpVideoUrl = _stmt.getText(_columnIndexOfVideoUrl);
          }
          final String _tmpTelegramFileId;
          if (_stmt.isNull(_columnIndexOfTelegramFileId)) {
            _tmpTelegramFileId = null;
          } else {
            _tmpTelegramFileId = _stmt.getText(_columnIndexOfTelegramFileId);
          }
          final long _tmpDuration;
          _tmpDuration = _stmt.getLong(_columnIndexOfDuration);
          final long _tmpSize;
          _tmpSize = _stmt.getLong(_columnIndexOfSize);
          final String _tmpLocalPath;
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null;
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath);
          }
          final boolean _tmpIsDownloaded;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsDownloaded));
          _tmpIsDownloaded = _tmp != 0;
          final float _tmpDownloadProgress;
          _tmpDownloadProgress = (float) (_stmt.getDouble(_columnIndexOfDownloadProgress));
          final String _tmpSourceLabel;
          if (_stmt.isNull(_columnIndexOfSourceLabel)) {
            _tmpSourceLabel = null;
          } else {
            _tmpSourceLabel = _stmt.getText(_columnIndexOfSourceLabel);
          }
          final boolean _tmpIsStreamable;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfIsStreamable));
          _tmpIsStreamable = _tmp_1 != 0;
          final int _tmpUploadDate;
          _tmpUploadDate = (int) (_stmt.getLong(_columnIndexOfUploadDate));
          _item = new VideoEntity(_tmpId,_tmpTitle,_tmpThumbnail,_tmpVideoUrl,_tmpTelegramFileId,_tmpDuration,_tmpSize,_tmpLocalPath,_tmpIsDownloaded,_tmpDownloadProgress,_tmpSourceLabel,_tmpIsStreamable,_tmpUploadDate);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object countDownloadedVideos(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM videos WHERE isDownloaded = 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final Integer _result;
        if (_stmt.step()) {
          final Integer _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(0));
          }
          _result = _tmp;
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object totalDownloadedSize(final Continuation<? super Long> $completion) {
    final String _sql = "SELECT COALESCE(SUM(size), 0) FROM videos WHERE isDownloaded = 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final Long _result;
        if (_stmt.step()) {
          final Long _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(0);
          }
          _result = _tmp;
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getWatchProgress(final String videoId,
      final Continuation<? super WatchProgressEntity> $completion) {
    final String _sql = "SELECT * FROM watch_progress WHERE videoId = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (videoId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, videoId);
        }
        final int _columnIndexOfVideoId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoId");
        final int _columnIndexOfLastPosition = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastPosition");
        final int _columnIndexOfDuration = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "duration");
        final int _columnIndexOfLastWatchedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastWatchedAt");
        final WatchProgressEntity _result;
        if (_stmt.step()) {
          final String _tmpVideoId;
          if (_stmt.isNull(_columnIndexOfVideoId)) {
            _tmpVideoId = null;
          } else {
            _tmpVideoId = _stmt.getText(_columnIndexOfVideoId);
          }
          final long _tmpLastPosition;
          _tmpLastPosition = _stmt.getLong(_columnIndexOfLastPosition);
          final long _tmpDuration;
          _tmpDuration = _stmt.getLong(_columnIndexOfDuration);
          final long _tmpLastWatchedAt;
          _tmpLastWatchedAt = _stmt.getLong(_columnIndexOfLastWatchedAt);
          _result = new WatchProgressEntity(_tmpVideoId,_tmpLastPosition,_tmpDuration,_tmpLastWatchedAt);
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Flow<List<WatchProgressEntity>> getAllWatchProgress() {
    final String _sql = "SELECT * FROM watch_progress ORDER BY lastWatchedAt DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"watch_progress"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfVideoId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoId");
        final int _columnIndexOfLastPosition = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastPosition");
        final int _columnIndexOfDuration = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "duration");
        final int _columnIndexOfLastWatchedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastWatchedAt");
        final List<WatchProgressEntity> _result = new ArrayList<WatchProgressEntity>();
        while (_stmt.step()) {
          final WatchProgressEntity _item;
          final String _tmpVideoId;
          if (_stmt.isNull(_columnIndexOfVideoId)) {
            _tmpVideoId = null;
          } else {
            _tmpVideoId = _stmt.getText(_columnIndexOfVideoId);
          }
          final long _tmpLastPosition;
          _tmpLastPosition = _stmt.getLong(_columnIndexOfLastPosition);
          final long _tmpDuration;
          _tmpDuration = _stmt.getLong(_columnIndexOfDuration);
          final long _tmpLastWatchedAt;
          _tmpLastWatchedAt = _stmt.getLong(_columnIndexOfLastWatchedAt);
          _item = new WatchProgressEntity(_tmpVideoId,_tmpLastPosition,_tmpDuration,_tmpLastWatchedAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<DownloadEntity>> getAllDownloads() {
    final String _sql = "SELECT * FROM downloads";
    return FlowUtil.createFlow(__db, false, new String[] {"downloads"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfVideoId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoId");
        final int _columnIndexOfState = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "state");
        final int _columnIndexOfProgress = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "progress");
        final int _columnIndexOfLocalPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localPath");
        final int _columnIndexOfErrorCode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "errorCode");
        final int _columnIndexOfDownloadId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "downloadId");
        final List<DownloadEntity> _result = new ArrayList<DownloadEntity>();
        while (_stmt.step()) {
          final DownloadEntity _item;
          final String _tmpVideoId;
          if (_stmt.isNull(_columnIndexOfVideoId)) {
            _tmpVideoId = null;
          } else {
            _tmpVideoId = _stmt.getText(_columnIndexOfVideoId);
          }
          final String _tmpState;
          if (_stmt.isNull(_columnIndexOfState)) {
            _tmpState = null;
          } else {
            _tmpState = _stmt.getText(_columnIndexOfState);
          }
          final float _tmpProgress;
          _tmpProgress = (float) (_stmt.getDouble(_columnIndexOfProgress));
          final String _tmpLocalPath;
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null;
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath);
          }
          final Integer _tmpErrorCode;
          if (_stmt.isNull(_columnIndexOfErrorCode)) {
            _tmpErrorCode = null;
          } else {
            _tmpErrorCode = (int) (_stmt.getLong(_columnIndexOfErrorCode));
          }
          final Long _tmpDownloadId;
          if (_stmt.isNull(_columnIndexOfDownloadId)) {
            _tmpDownloadId = null;
          } else {
            _tmpDownloadId = _stmt.getLong(_columnIndexOfDownloadId);
          }
          _item = new DownloadEntity(_tmpVideoId,_tmpState,_tmpProgress,_tmpLocalPath,_tmpErrorCode,_tmpDownloadId);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<DownloadEntity>> getActiveDownloads() {
    final String _sql = "SELECT * FROM downloads WHERE state IN ('DOWNLOADING', 'QUEUED', 'PAUSED')";
    return FlowUtil.createFlow(__db, false, new String[] {"downloads"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfVideoId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoId");
        final int _columnIndexOfState = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "state");
        final int _columnIndexOfProgress = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "progress");
        final int _columnIndexOfLocalPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localPath");
        final int _columnIndexOfErrorCode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "errorCode");
        final int _columnIndexOfDownloadId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "downloadId");
        final List<DownloadEntity> _result = new ArrayList<DownloadEntity>();
        while (_stmt.step()) {
          final DownloadEntity _item;
          final String _tmpVideoId;
          if (_stmt.isNull(_columnIndexOfVideoId)) {
            _tmpVideoId = null;
          } else {
            _tmpVideoId = _stmt.getText(_columnIndexOfVideoId);
          }
          final String _tmpState;
          if (_stmt.isNull(_columnIndexOfState)) {
            _tmpState = null;
          } else {
            _tmpState = _stmt.getText(_columnIndexOfState);
          }
          final float _tmpProgress;
          _tmpProgress = (float) (_stmt.getDouble(_columnIndexOfProgress));
          final String _tmpLocalPath;
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null;
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath);
          }
          final Integer _tmpErrorCode;
          if (_stmt.isNull(_columnIndexOfErrorCode)) {
            _tmpErrorCode = null;
          } else {
            _tmpErrorCode = (int) (_stmt.getLong(_columnIndexOfErrorCode));
          }
          final Long _tmpDownloadId;
          if (_stmt.isNull(_columnIndexOfDownloadId)) {
            _tmpDownloadId = null;
          } else {
            _tmpDownloadId = _stmt.getLong(_columnIndexOfDownloadId);
          }
          _item = new DownloadEntity(_tmpVideoId,_tmpState,_tmpProgress,_tmpLocalPath,_tmpErrorCode,_tmpDownloadId);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object getDownload(final String videoId,
      final Continuation<? super DownloadEntity> $completion) {
    final String _sql = "SELECT * FROM downloads WHERE videoId = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (videoId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, videoId);
        }
        final int _columnIndexOfVideoId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoId");
        final int _columnIndexOfState = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "state");
        final int _columnIndexOfProgress = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "progress");
        final int _columnIndexOfLocalPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localPath");
        final int _columnIndexOfErrorCode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "errorCode");
        final int _columnIndexOfDownloadId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "downloadId");
        final DownloadEntity _result;
        if (_stmt.step()) {
          final String _tmpVideoId;
          if (_stmt.isNull(_columnIndexOfVideoId)) {
            _tmpVideoId = null;
          } else {
            _tmpVideoId = _stmt.getText(_columnIndexOfVideoId);
          }
          final String _tmpState;
          if (_stmt.isNull(_columnIndexOfState)) {
            _tmpState = null;
          } else {
            _tmpState = _stmt.getText(_columnIndexOfState);
          }
          final float _tmpProgress;
          _tmpProgress = (float) (_stmt.getDouble(_columnIndexOfProgress));
          final String _tmpLocalPath;
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null;
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath);
          }
          final Integer _tmpErrorCode;
          if (_stmt.isNull(_columnIndexOfErrorCode)) {
            _tmpErrorCode = null;
          } else {
            _tmpErrorCode = (int) (_stmt.getLong(_columnIndexOfErrorCode));
          }
          final Long _tmpDownloadId;
          if (_stmt.isNull(_columnIndexOfDownloadId)) {
            _tmpDownloadId = null;
          } else {
            _tmpDownloadId = _stmt.getLong(_columnIndexOfDownloadId);
          }
          _result = new DownloadEntity(_tmpVideoId,_tmpState,_tmpProgress,_tmpLocalPath,_tmpErrorCode,_tmpDownloadId);
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Flow<List<VideoEntity>> getFavoriteVideos() {
    final String _sql = "\n"
            + "        SELECT v.* FROM videos v\n"
            + "        INNER JOIN favorites f ON v.id = f.videoId\n"
            + "        ORDER BY f.addedAt DESC\n"
            + "    ";
    return FlowUtil.createFlow(__db, false, new String[] {"videos", "favorites"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfThumbnail = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "thumbnail");
        final int _columnIndexOfVideoUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoUrl");
        final int _columnIndexOfTelegramFileId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "telegramFileId");
        final int _columnIndexOfDuration = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "duration");
        final int _columnIndexOfSize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "size");
        final int _columnIndexOfLocalPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localPath");
        final int _columnIndexOfIsDownloaded = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isDownloaded");
        final int _columnIndexOfDownloadProgress = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "downloadProgress");
        final int _columnIndexOfSourceLabel = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sourceLabel");
        final int _columnIndexOfIsStreamable = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isStreamable");
        final int _columnIndexOfUploadDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uploadDate");
        final List<VideoEntity> _result = new ArrayList<VideoEntity>();
        while (_stmt.step()) {
          final VideoEntity _item;
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpThumbnail;
          if (_stmt.isNull(_columnIndexOfThumbnail)) {
            _tmpThumbnail = null;
          } else {
            _tmpThumbnail = _stmt.getText(_columnIndexOfThumbnail);
          }
          final String _tmpVideoUrl;
          if (_stmt.isNull(_columnIndexOfVideoUrl)) {
            _tmpVideoUrl = null;
          } else {
            _tmpVideoUrl = _stmt.getText(_columnIndexOfVideoUrl);
          }
          final String _tmpTelegramFileId;
          if (_stmt.isNull(_columnIndexOfTelegramFileId)) {
            _tmpTelegramFileId = null;
          } else {
            _tmpTelegramFileId = _stmt.getText(_columnIndexOfTelegramFileId);
          }
          final long _tmpDuration;
          _tmpDuration = _stmt.getLong(_columnIndexOfDuration);
          final long _tmpSize;
          _tmpSize = _stmt.getLong(_columnIndexOfSize);
          final String _tmpLocalPath;
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null;
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath);
          }
          final boolean _tmpIsDownloaded;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsDownloaded));
          _tmpIsDownloaded = _tmp != 0;
          final float _tmpDownloadProgress;
          _tmpDownloadProgress = (float) (_stmt.getDouble(_columnIndexOfDownloadProgress));
          final String _tmpSourceLabel;
          if (_stmt.isNull(_columnIndexOfSourceLabel)) {
            _tmpSourceLabel = null;
          } else {
            _tmpSourceLabel = _stmt.getText(_columnIndexOfSourceLabel);
          }
          final boolean _tmpIsStreamable;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfIsStreamable));
          _tmpIsStreamable = _tmp_1 != 0;
          final int _tmpUploadDate;
          _tmpUploadDate = (int) (_stmt.getLong(_columnIndexOfUploadDate));
          _item = new VideoEntity(_tmpId,_tmpTitle,_tmpThumbnail,_tmpVideoUrl,_tmpTelegramFileId,_tmpDuration,_tmpSize,_tmpLocalPath,_tmpIsDownloaded,_tmpDownloadProgress,_tmpSourceLabel,_tmpIsStreamable,_tmpUploadDate);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<FavoriteEntity>> getAllFavorites() {
    final String _sql = "SELECT * FROM favorites ORDER BY addedAt DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"favorites"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfVideoId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoId");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfThumbnail = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "thumbnail");
        final int _columnIndexOfSource = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "source");
        final int _columnIndexOfAddedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "addedAt");
        final List<FavoriteEntity> _result = new ArrayList<FavoriteEntity>();
        while (_stmt.step()) {
          final FavoriteEntity _item;
          final String _tmpVideoId;
          if (_stmt.isNull(_columnIndexOfVideoId)) {
            _tmpVideoId = null;
          } else {
            _tmpVideoId = _stmt.getText(_columnIndexOfVideoId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpThumbnail;
          if (_stmt.isNull(_columnIndexOfThumbnail)) {
            _tmpThumbnail = null;
          } else {
            _tmpThumbnail = _stmt.getText(_columnIndexOfThumbnail);
          }
          final String _tmpSource;
          if (_stmt.isNull(_columnIndexOfSource)) {
            _tmpSource = null;
          } else {
            _tmpSource = _stmt.getText(_columnIndexOfSource);
          }
          final long _tmpAddedAt;
          _tmpAddedAt = _stmt.getLong(_columnIndexOfAddedAt);
          _item = new FavoriteEntity(_tmpVideoId,_tmpTitle,_tmpThumbnail,_tmpSource,_tmpAddedAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object isFavorite(final String videoId, final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM favorites WHERE videoId = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (videoId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, videoId);
        }
        final Integer _result;
        if (_stmt.step()) {
          final Integer _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(0));
          }
          _result = _tmp;
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Flow<List<VideoEntity>> getWatchLaterVideos() {
    final String _sql = "\n"
            + "        SELECT v.* FROM videos v\n"
            + "        INNER JOIN watch_later wl ON v.id = wl.videoId\n"
            + "        ORDER BY wl.addedAt DESC\n"
            + "    ";
    return FlowUtil.createFlow(__db, false, new String[] {"videos",
        "watch_later"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfThumbnail = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "thumbnail");
        final int _columnIndexOfVideoUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoUrl");
        final int _columnIndexOfTelegramFileId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "telegramFileId");
        final int _columnIndexOfDuration = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "duration");
        final int _columnIndexOfSize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "size");
        final int _columnIndexOfLocalPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localPath");
        final int _columnIndexOfIsDownloaded = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isDownloaded");
        final int _columnIndexOfDownloadProgress = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "downloadProgress");
        final int _columnIndexOfSourceLabel = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sourceLabel");
        final int _columnIndexOfIsStreamable = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isStreamable");
        final int _columnIndexOfUploadDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uploadDate");
        final List<VideoEntity> _result = new ArrayList<VideoEntity>();
        while (_stmt.step()) {
          final VideoEntity _item;
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpThumbnail;
          if (_stmt.isNull(_columnIndexOfThumbnail)) {
            _tmpThumbnail = null;
          } else {
            _tmpThumbnail = _stmt.getText(_columnIndexOfThumbnail);
          }
          final String _tmpVideoUrl;
          if (_stmt.isNull(_columnIndexOfVideoUrl)) {
            _tmpVideoUrl = null;
          } else {
            _tmpVideoUrl = _stmt.getText(_columnIndexOfVideoUrl);
          }
          final String _tmpTelegramFileId;
          if (_stmt.isNull(_columnIndexOfTelegramFileId)) {
            _tmpTelegramFileId = null;
          } else {
            _tmpTelegramFileId = _stmt.getText(_columnIndexOfTelegramFileId);
          }
          final long _tmpDuration;
          _tmpDuration = _stmt.getLong(_columnIndexOfDuration);
          final long _tmpSize;
          _tmpSize = _stmt.getLong(_columnIndexOfSize);
          final String _tmpLocalPath;
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null;
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath);
          }
          final boolean _tmpIsDownloaded;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsDownloaded));
          _tmpIsDownloaded = _tmp != 0;
          final float _tmpDownloadProgress;
          _tmpDownloadProgress = (float) (_stmt.getDouble(_columnIndexOfDownloadProgress));
          final String _tmpSourceLabel;
          if (_stmt.isNull(_columnIndexOfSourceLabel)) {
            _tmpSourceLabel = null;
          } else {
            _tmpSourceLabel = _stmt.getText(_columnIndexOfSourceLabel);
          }
          final boolean _tmpIsStreamable;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfIsStreamable));
          _tmpIsStreamable = _tmp_1 != 0;
          final int _tmpUploadDate;
          _tmpUploadDate = (int) (_stmt.getLong(_columnIndexOfUploadDate));
          _item = new VideoEntity(_tmpId,_tmpTitle,_tmpThumbnail,_tmpVideoUrl,_tmpTelegramFileId,_tmpDuration,_tmpSize,_tmpLocalPath,_tmpIsDownloaded,_tmpDownloadProgress,_tmpSourceLabel,_tmpIsStreamable,_tmpUploadDate);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<WatchLaterEntity>> getAllWatchLater() {
    final String _sql = "SELECT * FROM watch_later ORDER BY addedAt DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"watch_later"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfVideoId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoId");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfThumbnail = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "thumbnail");
        final int _columnIndexOfSource = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "source");
        final int _columnIndexOfAddedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "addedAt");
        final List<WatchLaterEntity> _result = new ArrayList<WatchLaterEntity>();
        while (_stmt.step()) {
          final WatchLaterEntity _item;
          final String _tmpVideoId;
          if (_stmt.isNull(_columnIndexOfVideoId)) {
            _tmpVideoId = null;
          } else {
            _tmpVideoId = _stmt.getText(_columnIndexOfVideoId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpThumbnail;
          if (_stmt.isNull(_columnIndexOfThumbnail)) {
            _tmpThumbnail = null;
          } else {
            _tmpThumbnail = _stmt.getText(_columnIndexOfThumbnail);
          }
          final String _tmpSource;
          if (_stmt.isNull(_columnIndexOfSource)) {
            _tmpSource = null;
          } else {
            _tmpSource = _stmt.getText(_columnIndexOfSource);
          }
          final long _tmpAddedAt;
          _tmpAddedAt = _stmt.getLong(_columnIndexOfAddedAt);
          _item = new WatchLaterEntity(_tmpVideoId,_tmpTitle,_tmpThumbnail,_tmpSource,_tmpAddedAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object isInWatchLater(final String videoId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM watch_later WHERE videoId = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (videoId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, videoId);
        }
        final Integer _result;
        if (_stmt.step()) {
          final Integer _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(0));
          }
          _result = _tmp;
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Flow<List<VideoEntity>> getHistoryVideos() {
    final String _sql = "\n"
            + "        SELECT v.* FROM videos v\n"
            + "        INNER JOIN watch_history wh ON v.id = wh.videoId\n"
            + "        ORDER BY wh.watchedAt DESC\n"
            + "    ";
    return FlowUtil.createFlow(__db, false, new String[] {"videos",
        "watch_history"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfThumbnail = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "thumbnail");
        final int _columnIndexOfVideoUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoUrl");
        final int _columnIndexOfTelegramFileId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "telegramFileId");
        final int _columnIndexOfDuration = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "duration");
        final int _columnIndexOfSize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "size");
        final int _columnIndexOfLocalPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localPath");
        final int _columnIndexOfIsDownloaded = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isDownloaded");
        final int _columnIndexOfDownloadProgress = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "downloadProgress");
        final int _columnIndexOfSourceLabel = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sourceLabel");
        final int _columnIndexOfIsStreamable = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isStreamable");
        final int _columnIndexOfUploadDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uploadDate");
        final List<VideoEntity> _result = new ArrayList<VideoEntity>();
        while (_stmt.step()) {
          final VideoEntity _item;
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpThumbnail;
          if (_stmt.isNull(_columnIndexOfThumbnail)) {
            _tmpThumbnail = null;
          } else {
            _tmpThumbnail = _stmt.getText(_columnIndexOfThumbnail);
          }
          final String _tmpVideoUrl;
          if (_stmt.isNull(_columnIndexOfVideoUrl)) {
            _tmpVideoUrl = null;
          } else {
            _tmpVideoUrl = _stmt.getText(_columnIndexOfVideoUrl);
          }
          final String _tmpTelegramFileId;
          if (_stmt.isNull(_columnIndexOfTelegramFileId)) {
            _tmpTelegramFileId = null;
          } else {
            _tmpTelegramFileId = _stmt.getText(_columnIndexOfTelegramFileId);
          }
          final long _tmpDuration;
          _tmpDuration = _stmt.getLong(_columnIndexOfDuration);
          final long _tmpSize;
          _tmpSize = _stmt.getLong(_columnIndexOfSize);
          final String _tmpLocalPath;
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null;
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath);
          }
          final boolean _tmpIsDownloaded;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsDownloaded));
          _tmpIsDownloaded = _tmp != 0;
          final float _tmpDownloadProgress;
          _tmpDownloadProgress = (float) (_stmt.getDouble(_columnIndexOfDownloadProgress));
          final String _tmpSourceLabel;
          if (_stmt.isNull(_columnIndexOfSourceLabel)) {
            _tmpSourceLabel = null;
          } else {
            _tmpSourceLabel = _stmt.getText(_columnIndexOfSourceLabel);
          }
          final boolean _tmpIsStreamable;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfIsStreamable));
          _tmpIsStreamable = _tmp_1 != 0;
          final int _tmpUploadDate;
          _tmpUploadDate = (int) (_stmt.getLong(_columnIndexOfUploadDate));
          _item = new VideoEntity(_tmpId,_tmpTitle,_tmpThumbnail,_tmpVideoUrl,_tmpTelegramFileId,_tmpDuration,_tmpSize,_tmpLocalPath,_tmpIsDownloaded,_tmpDownloadProgress,_tmpSourceLabel,_tmpIsStreamable,_tmpUploadDate);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<WatchHistoryEntity>> getAllWatchHistory() {
    final String _sql = "SELECT * FROM watch_history ORDER BY watchedAt DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"watch_history"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfVideoId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoId");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfThumbnail = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "thumbnail");
        final int _columnIndexOfSource = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "source");
        final int _columnIndexOfWatchedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "watchedAt");
        final List<WatchHistoryEntity> _result = new ArrayList<WatchHistoryEntity>();
        while (_stmt.step()) {
          final WatchHistoryEntity _item;
          final String _tmpVideoId;
          if (_stmt.isNull(_columnIndexOfVideoId)) {
            _tmpVideoId = null;
          } else {
            _tmpVideoId = _stmt.getText(_columnIndexOfVideoId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpThumbnail;
          if (_stmt.isNull(_columnIndexOfThumbnail)) {
            _tmpThumbnail = null;
          } else {
            _tmpThumbnail = _stmt.getText(_columnIndexOfThumbnail);
          }
          final String _tmpSource;
          if (_stmt.isNull(_columnIndexOfSource)) {
            _tmpSource = null;
          } else {
            _tmpSource = _stmt.getText(_columnIndexOfSource);
          }
          final long _tmpWatchedAt;
          _tmpWatchedAt = _stmt.getLong(_columnIndexOfWatchedAt);
          _item = new WatchHistoryEntity(_tmpVideoId,_tmpTitle,_tmpThumbnail,_tmpSource,_tmpWatchedAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object deleteAllDownloads(final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM downloads";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteAllVideos(final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM videos";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteAllWatchProgress(final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM watch_progress";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteWatchProgress(final String videoId,
      final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM watch_progress WHERE videoId = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (videoId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, videoId);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteDownload(final String videoId, final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM downloads WHERE videoId = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (videoId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, videoId);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object removeFavorite(final String videoId, final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM favorites WHERE videoId = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (videoId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, videoId);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteAllFavorites(final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM favorites";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object removeFromWatchLater(final String videoId,
      final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM watch_later WHERE videoId = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (videoId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, videoId);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteAllWatchLater(final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM watch_later";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object removeFromWatchHistory(final String videoId,
      final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM watch_history WHERE videoId = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (videoId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, videoId);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteAllWatchHistory(final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM watch_history";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
