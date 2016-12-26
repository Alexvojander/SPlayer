package com.dmplayer.db;

import android.database.Cursor;

import com.dmplayer.models.Playlist;

public interface PlaylistSongsTableHelper {

    String TABLE_NAME = "local_playlist_song";

    String ID = "_id";
    String PLAYLIST_ID = "local_playlist_id";
    String SONG_ID = "local_song_id";

    void insertPlaylistSongs(Playlist playlist, long id);
    Cursor getPlaylistsSongs(long playlistId);
}
