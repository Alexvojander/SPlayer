package com.dmplayer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.dmplayer.DMPlayerApplication;
import com.dmplayer.models.Playlist;
import com.dmplayer.models.SongDetail;

import java.util.ArrayList;
import java.util.List;

import static com.dmplayer.utility.DMPlayerUtility.closeCursor;

public class LocalPlaylistTablesHelper {

    private static final String TAG = LocalPlaylistTablesHelper.class.getSimpleName();

    private SQLiteDatabase db;

    private PlaylistTableHelperImpl playlistTable;
    private SongsTableHelperImpl songsTable;
    private PlaylistSongsTableHelperImpl playlistSongsTable;

    private static LocalPlaylistTablesHelper instance;

    public static synchronized LocalPlaylistTablesHelper getInstance(Context context) {
        if (instance == null) {
            instance = new LocalPlaylistTablesHelper(context);
        }
        return instance;
    }

    public LocalPlaylistTablesHelper(Context context) {
        DMPLayerDBHelper dbHelper = ((DMPlayerApplication) context.getApplicationContext()).DB_HELPER;
        db = dbHelper.getDB();

        playlistTable = new PlaylistTableHelperImpl(db);
        songsTable = new SongsTableHelperImpl(db);
        playlistSongsTable = new PlaylistSongsTableHelperImpl(db);
    }

    public long insertPlaylist(Playlist playlist) {
        long id = -1;
        try {
            db.beginTransaction();

            id = playlistTable.insertPlaylist(playlist);
            playlistSongsTable.insertPlaylistSongs(playlist, id);
            songsTable.insertSongs(playlist);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            db.endTransaction();
        }
        return id;
    }

    public boolean deletePlaylist(long id) {
        return playlistTable.deletePlaylist(id);
    }

    public List<Playlist> getLocalPlaylistsEmpty() {
        Cursor playlistCursor = playlistTable.getPlaylists();

        List<Playlist> temp = new ArrayList<>();
        try {
            if (playlistCursor != null) {
                int _id = playlistCursor.getColumnIndex(PlaylistTableHelper.ID);
                int name = playlistCursor.getColumnIndex(PlaylistTableHelper.NAME);

                while (playlistCursor.moveToNext()) {
                    final int ID = playlistCursor.getInt(_id);
                    final String NAME = playlistCursor.getString(name);

                    temp.add(new Playlist.Builder()
                            .setId(ID)
                            .setName(NAME)
                            .build());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            closeCursor(playlistCursor);
        }
        return temp;
    }

    public Playlist getPlaylist(long id) {
        Cursor playlistSongsCursor = playlistSongsTable.getPlaylistsSongs(id);
        List<String> playlistIds = getSongIdsFromCursor(playlistSongsCursor);

        Cursor songsCursor = songsTable.getSongsList(playlistIds.toArray(new String[0]));
        List<SongDetail> songs = getSongsFromCursor(songsCursor);

        Cursor playlistCursor = playlistTable.getPlaylists();
        String name = getPlaylistNameFromCursor(playlistCursor);

        return new Playlist.Builder()
                        .setId(id)
                        .setName(name)
                        .setSongs(songs)
                        .build();
    }

    public List<SongDetail> getSongList(long id) {
        Cursor playlistSongsCursor = playlistSongsTable.getPlaylistsSongs(id);
        List<String> playlistIds = getSongIdsFromCursor(playlistSongsCursor);

        Cursor songsCursor = songsTable.getSongsList(playlistIds.toArray(new String[0]));
        return getSongsFromCursor(songsCursor);
    }

    private List<String> getSongIdsFromCursor(Cursor cursor) {
        List<String> temp = new ArrayList<>();
        try {
            if (cursor != null) {
                int _id = cursor.getColumnIndex(PlaylistSongsTableHelper.SONG_ID);

                while (cursor.moveToNext()) {
                    final int SONG_ID = cursor.getInt(_id);
                    temp.add(String.valueOf(SONG_ID));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            closeCursor(cursor);
        }
        return temp;
    }

    private List<SongDetail> getSongsFromCursor(Cursor cursor) {
        List<SongDetail> temp = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() >= 1) {
                int _id = cursor.getColumnIndex(SongsTableHelper.ID);
                int artist = cursor.getColumnIndex(SongsTableHelper.ARTIST);
                int album_id = cursor.getColumnIndex(SongsTableHelper.ALBUM_ID);
                int title = cursor.getColumnIndex(SongsTableHelper.TITLE);
                int data = cursor.getColumnIndex(SongsTableHelper.PATH);
                int display_name = cursor.getColumnIndex(SongsTableHelper.DISPLAY_NAME);
                int duration = cursor.getColumnIndex(SongsTableHelper.DURATION);

                while (cursor.moveToNext()) {
                    final int ID = cursor.getInt(_id);
                    final String ARTIST = cursor.getString(artist);
                    final String TITLE = cursor.getString(title);
                    final String DISPLAY_NAME = cursor.getString(display_name);
                    final String DURATION = cursor.getString(duration);
                    final String PATH = cursor.getString(data);

                    SongDetail song = new SongDetail(ID, album_id, ARTIST, TITLE, PATH, DISPLAY_NAME, DURATION);
                    temp.add(song);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            closeCursor(cursor);
        }
        return temp;
    }

    private String getPlaylistNameFromCursor(Cursor cursor) {
        String temp = "";
        try {
            if (cursor != null) {
                int name = cursor.getColumnIndex(PlaylistTableHelper.NAME);
                cursor.moveToFirst();
                temp = cursor.getString(name);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            closeCursor(cursor);
        }
        return temp;
    }


    private class PlaylistTableHelperImpl implements PlaylistTableHelper {

        private SQLiteDatabase db;

        public PlaylistTableHelperImpl(SQLiteDatabase db) {
            this.db = db;
        }

        @Override
        public long insertPlaylist(Playlist playlist) {
            ContentValues playlistInsertion = new ContentValues();
            playlistInsertion.put(NAME, playlist.getName());

            return db.insert(TABLE_NAME, null, playlistInsertion);
        }

        @Override
        public Cursor getPlaylists() {
            String sqlQuery = "SELECT * FROM " + TABLE_NAME;

            Cursor cursor = null;
            try {
                cursor = db.rawQuery(sqlQuery, null);
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
            return cursor;
        }

        @Override
        public Cursor getPlaylist(long id) {
            String sqlQuery = "SELECT " + NAME + " FROM " + TABLE_NAME + " WHERE " + ID + "=" + id;

            Cursor cursor = null;
            try {
                cursor = db.rawQuery(sqlQuery, null);
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
            return cursor;
        }

        @Override
        public boolean deletePlaylist(long id) {
            String sqlStatement = "DELETE FROM " + TABLE_NAME + " WHERE " + ID + "=" + id;

            try {
                db.execSQL(sqlStatement);
            } catch (SQLException e) {
                Log.e(TAG, Log.getStackTraceString(e));
                return false;
            }

            return true;
        }
    }

    private class SongsTableHelperImpl implements SongsTableHelper{

        private SQLiteDatabase db;

        public SongsTableHelperImpl(SQLiteDatabase db) {
            this.db = db;
        }

        @Override
        public void insertSongs(Playlist playlist) {
//          db.insertWithOnConflict(,,,SQLiteDatabase.CONFLICT_IGNORE);
            String sql = "INSERT OR IGNORE INTO " + TABLE_NAME + " VALUES (?,?,?,?,?,?,?);";

            SQLiteStatement insert = db.compileStatement(sql);

            for (SongDetail song : playlist.getSongs()) {
                if (song != null) {
                    insert.clearBindings();
                    insert.bindLong(1, song.getId());
                    insert.bindLong(2, song.getAlbumId());
                    insert.bindString(3, song.getArtist());
                    insert.bindString(4, song.getTitle());
                    insert.bindString(5, song.getDisplayName());
                    insert.bindString(6, song.getDuration());
                    insert.bindString(7, song.getPath());

                    insert.execute();
                }
            }
        }

        @Override
        public Cursor getSongsList(String... ids) {
            StringBuilder sqlQuery = new StringBuilder("SELECT * FROM " + TABLE_NAME + " WHERE " + ID + " IN (");
            for (int i = 0; i < ids.length; i++) {
                sqlQuery.append("?");

                if (i != ids.length - 1) {
                    sqlQuery.append(",");
                }
            }
            sqlQuery.append(")");

            Cursor cursor = null;
            try {
                cursor = db.rawQuery(sqlQuery.toString(), ids);
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
            return cursor;
        }
    }

    private class PlaylistSongsTableHelperImpl implements PlaylistSongsTableHelper{

        private SQLiteDatabase db;

        public PlaylistSongsTableHelperImpl(SQLiteDatabase db) {
            this.db = db;
        }

        @Override
        public void insertPlaylistSongs(Playlist playlist, long id) {
            String sql = "INSERT INTO " + TABLE_NAME +
                    "(" + PLAYLIST_ID + ", " + SONG_ID +") VALUES (?,?);";

            SQLiteStatement insert = db.compileStatement(sql);
            for (SongDetail song : playlist.getSongs()) {
                    insert.clearBindings();

                    insert.bindLong(1, id);
                    insert.bindLong(2, song.getId());

                try {
                    insert.execute();
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
        }

        @Override
        public Cursor getPlaylistsSongs(long playlistId) {
            String sqlQuery = "SELECT " + SONG_ID + " FROM " + TABLE_NAME + " WHERE " + PLAYLIST_ID + "=?";

            Cursor cursor = null;
            try {
                cursor = db.rawQuery(sqlQuery, new String [] {String.valueOf(playlistId)});
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
            return cursor;
        }
    }
}
