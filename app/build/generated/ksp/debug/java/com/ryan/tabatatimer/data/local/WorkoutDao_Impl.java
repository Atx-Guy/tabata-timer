package com.ryan.tabatatimer.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.ryan.tabatatimer.model.Workout;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class WorkoutDao_Impl implements WorkoutDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Workout> __insertionAdapterOfWorkout;

  private final EntityDeletionOrUpdateAdapter<Workout> __deletionAdapterOfWorkout;

  private final EntityDeletionOrUpdateAdapter<Workout> __updateAdapterOfWorkout;

  public WorkoutDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWorkout = new EntityInsertionAdapter<Workout>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `workouts` (`id`,`name`,`workDurationSeconds`,`restDurationSeconds`,`rounds`,`warmupSeconds`,`colorCheck`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Workout entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getWorkDurationSeconds());
        statement.bindLong(4, entity.getRestDurationSeconds());
        statement.bindLong(5, entity.getRounds());
        statement.bindLong(6, entity.getWarmupSeconds());
        statement.bindLong(7, entity.getColorCheck());
      }
    };
    this.__deletionAdapterOfWorkout = new EntityDeletionOrUpdateAdapter<Workout>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `workouts` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Workout entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfWorkout = new EntityDeletionOrUpdateAdapter<Workout>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `workouts` SET `id` = ?,`name` = ?,`workDurationSeconds` = ?,`restDurationSeconds` = ?,`rounds` = ?,`warmupSeconds` = ?,`colorCheck` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Workout entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getWorkDurationSeconds());
        statement.bindLong(4, entity.getRestDurationSeconds());
        statement.bindLong(5, entity.getRounds());
        statement.bindLong(6, entity.getWarmupSeconds());
        statement.bindLong(7, entity.getColorCheck());
        statement.bindLong(8, entity.getId());
      }
    };
  }

  @Override
  public Object insertWorkout(final Workout workout, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWorkout.insert(workout);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteWorkout(final Workout workout, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfWorkout.handle(workout);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateWorkout(final Workout workout, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfWorkout.handle(workout);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Workout>> getAllWorkouts() {
    final String _sql = "SELECT * FROM workouts ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"workouts"}, new Callable<List<Workout>>() {
      @Override
      @NonNull
      public List<Workout> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfWorkDurationSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "workDurationSeconds");
          final int _cursorIndexOfRestDurationSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "restDurationSeconds");
          final int _cursorIndexOfRounds = CursorUtil.getColumnIndexOrThrow(_cursor, "rounds");
          final int _cursorIndexOfWarmupSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "warmupSeconds");
          final int _cursorIndexOfColorCheck = CursorUtil.getColumnIndexOrThrow(_cursor, "colorCheck");
          final List<Workout> _result = new ArrayList<Workout>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Workout _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final int _tmpWorkDurationSeconds;
            _tmpWorkDurationSeconds = _cursor.getInt(_cursorIndexOfWorkDurationSeconds);
            final int _tmpRestDurationSeconds;
            _tmpRestDurationSeconds = _cursor.getInt(_cursorIndexOfRestDurationSeconds);
            final int _tmpRounds;
            _tmpRounds = _cursor.getInt(_cursorIndexOfRounds);
            final int _tmpWarmupSeconds;
            _tmpWarmupSeconds = _cursor.getInt(_cursorIndexOfWarmupSeconds);
            final int _tmpColorCheck;
            _tmpColorCheck = _cursor.getInt(_cursorIndexOfColorCheck);
            _item = new Workout(_tmpId,_tmpName,_tmpWorkDurationSeconds,_tmpRestDurationSeconds,_tmpRounds,_tmpWarmupSeconds,_tmpColorCheck);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getWorkoutById(final long id, final Continuation<? super Workout> $completion) {
    final String _sql = "SELECT * FROM workouts WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Workout>() {
      @Override
      @Nullable
      public Workout call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfWorkDurationSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "workDurationSeconds");
          final int _cursorIndexOfRestDurationSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "restDurationSeconds");
          final int _cursorIndexOfRounds = CursorUtil.getColumnIndexOrThrow(_cursor, "rounds");
          final int _cursorIndexOfWarmupSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "warmupSeconds");
          final int _cursorIndexOfColorCheck = CursorUtil.getColumnIndexOrThrow(_cursor, "colorCheck");
          final Workout _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final int _tmpWorkDurationSeconds;
            _tmpWorkDurationSeconds = _cursor.getInt(_cursorIndexOfWorkDurationSeconds);
            final int _tmpRestDurationSeconds;
            _tmpRestDurationSeconds = _cursor.getInt(_cursorIndexOfRestDurationSeconds);
            final int _tmpRounds;
            _tmpRounds = _cursor.getInt(_cursorIndexOfRounds);
            final int _tmpWarmupSeconds;
            _tmpWarmupSeconds = _cursor.getInt(_cursorIndexOfWarmupSeconds);
            final int _tmpColorCheck;
            _tmpColorCheck = _cursor.getInt(_cursorIndexOfColorCheck);
            _result = new Workout(_tmpId,_tmpName,_tmpWorkDurationSeconds,_tmpRestDurationSeconds,_tmpRounds,_tmpWarmupSeconds,_tmpColorCheck);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
