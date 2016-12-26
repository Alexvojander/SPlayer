package com.dmplayer.db;

import android.database.Cursor;

import com.dmplayer.models.Playlist;

public interface PlaylistTableHelper {

    String TABLE_NAME = "local_playlist";

    String ID = "_id";
    String NAME = "name";

    long insertPlaylist(Playlist playlist);
    Cursor getPlaylists();
    Cursor getPlaylist(long id);
    boolean deletePlaylist(long id);
}
