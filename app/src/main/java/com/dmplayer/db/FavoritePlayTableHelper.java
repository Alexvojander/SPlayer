package com.dmplayer.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.dmplayer.DMPlayerApplication;
import com.dmplayer.models.SongDetail;

import static com.dmplayer.utility.DMPlayerUtility.closeCursor;

public class FavoritePlayTableHelper {
    public static final String TABLE_NAME = "song_favourite";

    public static final String ID = "_id";
    public static final String ALBUM_ID = "album_id";
    public static final String ARTIST = "artist";
    public static final String TITLE = "title";
    public static final String DISPLAY_NAME = "display_name";
    public static final String DURATION = "duration";
    public static final String PATH = "path";
    public static final String IS_FAVORITE = "is_favorite";

    private static final String TAG = FavoritePlayTableHelper.class.getSimpleName();

    private DMPLayerDBHelper dbHelper;
    private SQLiteDatabase sampleDB;

    private static FavoritePlayTableHelper instance;

    public static synchronized FavoritePlayTableHelper getInstance(Context context) {
        if (instance == null) {
            instance = new FavoritePlayTableHelper(context);
        }
        return instance;
    }

    private FavoritePlayTableHelper(Context context) {
        if (dbHelper == null) {
            dbHelper = ((DMPlayerApplication) context.getApplicationContext()).DB_HELPER;
        }
    }

    public void insertSong(SongDetail song, int isFav) {
        try {
            sampleDB = dbHelper.getDB();
            sampleDB.beginTransaction();

            String sql = "INSERT OR REPLACE INTO " + TABLE_NAME + " VALUES (?,?,?,?,?,?,?,?);";
            SQLiteStatement insert = sampleDB.compileStatement(sql);

            try {
                if (song != null) {
                    insert.clearBindings();
                    insert.bindLong(1, song.getId());
                    insert.bindLong(2, song.getAlbumId());
                    insert.bindString(3, song.getArtist());
                    insert.bindString(4, song.getTitle());
                    insert.bindString(5, song.getDisplayName());
                    insert.bindString(6, song.getDuration());
                    insert.bindString(7, song.getPath());
                    insert.bindLong(8, isFav);

                    insert.execute();
                }
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
            sampleDB.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            sampleDB.endTransaction();
        }
    }

    public Cursor getFavoriteSongList() {
        Cursor cursor = null;
        try {
            String sqlQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + IS_FAVORITE + "=1";
            sampleDB = dbHelper.getDB();
            cursor = sampleDB.rawQuery(sqlQuery, null);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return cursor;
    }

    public boolean isSongFavorite(SongDetail song) {
        Cursor cursor = null;
        try {
            String sqlQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + ID + "=" + song.getId() + " AND " + IS_FAVORITE + "=1";
            sampleDB = dbHelper.getDB();
            cursor = sampleDB.rawQuery(sqlQuery, null);
            if (cursor != null && cursor.getCount() >= 1) {
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            closeCursor(cursor);
        }
        return false;
    }
}
