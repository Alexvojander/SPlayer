package com.dmplayer.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dmplayer.R;


public class DMPLayerDBHelper extends SQLiteOpenHelper {
    private static final String TAG = DMPLayerDBHelper.class.getSimpleName();

    private static String DATABASE_NAME;
    private static int DATABASE_VERSION;

    private String DB_PATH;
    private Context context;
    private SQLiteDatabase db;

    public DMPLayerDBHelper(Context context) {
        super(context, context.getResources().getString(R.string.database_name), null, Integer.parseInt(context.getResources().getString(R.string.database_version)));
        this.context = context;

        DATABASE_NAME = context.getResources().getString(R.string.database_name);
        DATABASE_VERSION = Integer.parseInt(context.getResources().getString(R.string.database_version));
        DB_PATH = context.getDatabasePath(DATABASE_NAME).getPath();
        context.openOrCreateDatabase(DATABASE_NAME, SQLiteDatabase.OPEN_READWRITE, null);
    }

    private static String sqlForCreateMostPlay() {
        return "CREATE TABLE " + MostAndRecentPlayTableHelper.TABLE_NAME + " ("
                + MostAndRecentPlayTableHelper.ID + " INTEGER NOT NULL PRIMARY KEY,"
                + MostAndRecentPlayTableHelper.ALBUM_ID + " INTEGER NOT NULL,"
                + MostAndRecentPlayTableHelper.ARTIST + " TEXT NOT NULL,"
                + MostAndRecentPlayTableHelper.TITLE + " TEXT NOT NULL,"
                + MostAndRecentPlayTableHelper.DISPLAY_NAME + " TEXT NOT NULL,"
                + MostAndRecentPlayTableHelper.DURATION + " TEXT NOT NULL,"
                + MostAndRecentPlayTableHelper.PATH + " TEXT NOT NULL,"
                + MostAndRecentPlayTableHelper.PLAY_COUNT + " INTEGER NOT NULL);";
    }

    private static String sqlForCreateFavoritePlay() {
        return "CREATE TABLE " + FavoritePlayTableHelper.TABLE_NAME + " ("
                + FavoritePlayTableHelper.ID + " INTEGER NOT NULL PRIMARY KEY,"
                + FavoritePlayTableHelper.ALBUM_ID + " INTEGER NOT NULL,"
                + FavoritePlayTableHelper.ARTIST + " TEXT NOT NULL,"
                + FavoritePlayTableHelper.TITLE + " TEXT NOT NULL,"
                + FavoritePlayTableHelper.DISPLAY_NAME + " TEXT NOT NULL,"
                + FavoritePlayTableHelper.DURATION + " TEXT NOT NULL,"
                + FavoritePlayTableHelper.PATH + " TEXT NOT NULL,"
                + FavoritePlayTableHelper.IS_FAVORITE + " INTEGER NOT NULL);";
    }

    private static String sqlForCreatePlaylistTable() {
        return "CREATE TABLE " + PlaylistTableHelper.TABLE_NAME + " ("
                + PlaylistTableHelper.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PlaylistTableHelper.NAME + " TEXT NOT NULL);";
    }

    private static String sqlForCreatePlaylistSongsTable() {
        return "CREATE TABLE " + PlaylistSongsTableHelper.TABLE_NAME + " ("
                + PlaylistSongsTableHelper.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PlaylistSongsTableHelper.PLAYLIST_ID + " INTEGER NOT NULL,"
                + PlaylistSongsTableHelper.SONG_ID + " INTEGER NOT NULL,"
                + " FOREIGN KEY(" + PlaylistSongsTableHelper.PLAYLIST_ID + ")"
                + " REFERENCES " + PlaylistTableHelper.TABLE_NAME + "(" + PlaylistTableHelper.ID + ")"
                + " ON DELETE CASCADE,"
                + " FOREIGN KEY(" + PlaylistSongsTableHelper.SONG_ID + ")"
                + " REFERENCES " + SongsTableHelper.TABLE_NAME + "(" + SongsTableHelper.ID + ")"
                + " ON DELETE CASCADE);";
    }

    private static String sqlForCreateSongsTable() {
        return "CREATE TABLE " + SongsTableHelper.TABLE_NAME + " ("
                + SongsTableHelper.ID + " INTEGER NOT NULL PRIMARY KEY,"
                + SongsTableHelper.ALBUM_ID + " INTEGER NOT NULL,"
                + SongsTableHelper.ARTIST + " TEXT NOT NULL,"
                + SongsTableHelper.TITLE + " TEXT NOT NULL,"
                + SongsTableHelper.DISPLAY_NAME + " TEXT NOT NULL,"
                + SongsTableHelper.DURATION + " TEXT NOT NULL,"
                + SongsTableHelper.PATH + " TEXT NOT NULL);";
    }

    public SQLiteDatabase getDB() {
        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(sqlForCreateMostPlay());
            db.execSQL(sqlForCreateFavoritePlay());
            db.execSQL(sqlForCreatePlaylistTable());
            db.execSQL(sqlForCreatePlaylistSongsTable());
            db.execSQL(sqlForCreateSongsTable());
        } catch (SQLException e) {
            Log.e(TAG, "Error creating database:", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + MostAndRecentPlayTableHelper.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + FavoritePlayTableHelper.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + PlaylistTableHelper.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + PlaylistSongsTableHelper.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + SongsTableHelper.TABLE_NAME);
            onCreate(db);
        } catch (SQLException e) {
            Log.e(TAG, "Error updating database:", e);
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public synchronized void close() {
        if (getDB() != null) {
            getDB().close();
        }

        super.close();
    }

    public void openDataBase() throws SQLException {
        db = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }
}