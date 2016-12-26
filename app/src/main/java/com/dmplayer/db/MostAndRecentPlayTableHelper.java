package com.dmplayer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.dmplayer.DMPlayerApplication;
import com.dmplayer.models.SongDetail;

import static com.dmplayer.utility.DMPlayerUtility.closeCursor;

public class MostAndRecentPlayTableHelper {
    public static final String TABLE_NAME = "song_most_and_recent";

    public static final String ID = "_id";
    public static final String ALBUM_ID = "album_id";
    public static final String ARTIST = "artist";
    public static final String TITLE = "title";
    public static final String DISPLAY_NAME = "display_name";
    public static final String DURATION = "duration";
    public static final String PATH = "path";
    public static final String PLAY_COUNT = "play_count";

    private static final String TAG = MostAndRecentPlayTableHelper.class.getSimpleName();

    private DMPLayerDBHelper dbHelper;
    private SQLiteDatabase db;

    private static MostAndRecentPlayTableHelper instance;

    public static synchronized MostAndRecentPlayTableHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MostAndRecentPlayTableHelper(context);
        }
        return instance;
    }

    private MostAndRecentPlayTableHelper(Context context) {
        if (dbHelper == null) {
            dbHelper = ((DMPlayerApplication) context.getApplicationContext()).DB_HELPER;
        }
    }

    public void insertSong(SongDetail songDetail) {
        try {
            if (isSongExist(songDetail.getId())) {
                return;
            }

            db = dbHelper.getDB();
            db.beginTransaction();

            String sql = "INSERT OR REPLACE INTO " + TABLE_NAME + " VALUES (?,?,?,?,?,?,?,?);";
            SQLiteStatement insert = db.compileStatement(sql);

            try {
                insert.clearBindings();
                insert.bindLong(1, songDetail.getId());
                insert.bindLong(2, songDetail.getAlbumId());
                insert.bindString(3, songDetail.getArtist());
                insert.bindString(4, songDetail.getTitle());
                insert.bindString(5, songDetail.getDisplayName());
                insert.bindString(6, songDetail.getDuration());
                insert.bindString(7, songDetail.getPath());
                insert.bindLong(8, 1); //play count for the first time

                insert.execute();
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
            db.setTransactionSuccessful();

        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getMostPlayed() {
        Cursor cursor = null;
        try {
            String sqlQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + PLAY_COUNT + ">=2 ORDER BY " + PLAY_COUNT + " ASC LIMIT 20";
            db = dbHelper.getDB();
            cursor = db.rawQuery(sqlQuery, null);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return cursor;
    }

    private boolean isSongExist(int id) {
        Cursor cursor = null;
        boolean isExist = false;
        try {
            db = dbHelper.getDB();

            String sqlQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + ID + "=?";
            cursor = db.rawQuery(sqlQuery, new String[] {String.valueOf(id)});

            if (cursor != null && cursor.getCount() >= 1) {
                cursor.moveToNext();
                long count = cursor.getLong(cursor.getColumnIndex(PLAY_COUNT));
                count++;
                updateStatus(count, id);
                isExist = true;
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            closeCursor(cursor);
        }
        return isExist;
    }

    private void updateStatus(long count, int musicId) {
        try {
            ContentValues values = new ContentValues();
            values.put(PLAY_COUNT, count);
            db.update(TABLE_NAME, values, ID + "=?", new String[] {String.valueOf(musicId)});
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }
}
