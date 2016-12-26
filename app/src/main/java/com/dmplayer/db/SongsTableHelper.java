package com.dmplayer.db;

import android.database.Cursor;

import com.dmplayer.models.Playlist;

public interface SongsTableHelper {

    String TABLE_NAME = "local_song";

    String ID = "_id";
    String ALBUM_ID = "album_id";
    String ARTIST = "artist";
    String TITLE = "title";
    String DISPLAY_NAME = "display_name";
    String DURATION = "duration";
    String PATH = "path";

    void insertSongs(Playlist playlist);
    Cursor getSongsList(String... ids);
}