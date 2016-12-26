package com.dmplayer.utility;

import android.content.Context;
import android.os.AsyncTask;

import com.dmplayer.utility.asynctaskabstraction.AbstractAsyncTask;
import com.dmplayer.utility.asynctaskabstraction.TaskStateListener;
import com.dmplayer.db.LocalPlaylistTablesHelper;
import com.dmplayer.models.AsyncTaskResult;
import com.dmplayer.models.Playlist;
import com.dmplayer.models.SongDetail;
import com.dmplayer.models.playlisitems.DefaultPlaylistCategorySingle;
import com.dmplayer.phonemedia.PhoneMediaControl;

import java.util.List;

public final class DefaultPlaylistTaskFactory {

    private Context context;
    private TaskStateListener<Playlist> listener;

    public DefaultPlaylistTaskFactory(Context context, TaskStateListener<Playlist> listener) {
        this.context = context;
        this.listener = listener;
    }

    public AsyncTask<Void, Void, AsyncTaskResult<Playlist>> getTask(DefaultPlaylistCategorySingle category,
                                                                    final long id, final String name) {
        switch (category) {
            case ALL_SONGS:
                return new AbstractAsyncTask<Playlist>(listener) {
                    @Override
                    protected AsyncTaskResult<Playlist> doInBackground(Void... params) {
                        List<SongDetail> songs = PhoneMediaControl.getInstance()
                                .loadMusicList(context, id, PhoneMediaControl.SongsLoadFor.ALL, "");

                        return new AsyncTaskResult<>(
                                new Playlist.Builder()
                                        .setId(id)
                                        .setName(name)
                                        .setSongs(songs)
                                        .build());
                    }
                };

            case MOST_PLAYED:
                return new AbstractAsyncTask<Playlist>(listener) {
                    @Override
                    protected AsyncTaskResult<Playlist> doInBackground(Void... params) {
                        List<SongDetail> songs = PhoneMediaControl.getInstance()
                                .loadMusicList(context, id, PhoneMediaControl.SongsLoadFor.MOST_PLAY, "");

                        return new AsyncTaskResult<>(
                                new Playlist.Builder()
                                .setId(id)
                                .setName(name)
                                .setSongs(songs)
                                .build());
                    }
                };

            case LOCAL:
                return new AbstractAsyncTask<Playlist>(listener) {
                    @Override
                    protected AsyncTaskResult<Playlist> doInBackground(Void... params) {
                        try {
                            List<SongDetail> songs =  LocalPlaylistTablesHelper.getInstance(context)
                                    .getSongList(id);

                            return new AsyncTaskResult<>(
                                    new Playlist.Builder()
                                    .setId(id)
                                    .setName(name)
                                    .setSongs(songs)
                                    .build());
                        } catch (Exception e) {
                            return new AsyncTaskResult<>(e);
                        }
                    }
                };

            case GENRE:
                return new AbstractAsyncTask<Playlist>(listener) {
                    @Override
                    protected AsyncTaskResult<Playlist> doInBackground(Void... params) {
                        List<SongDetail> songs = PhoneMediaControl.getInstance()
                                .loadMusicList(context, id, PhoneMediaControl.SongsLoadFor.GENRE, "");

                        return new AsyncTaskResult<>(
                                new Playlist.Builder()
                                        .setId(id)
                                        .setName(name)
                                        .setSongs(songs)
                                        .build());
                    }
                };

            case ARTIST:
                return new AbstractAsyncTask<Playlist>(listener) {
                    @Override
                    protected AsyncTaskResult<Playlist> doInBackground(Void... params) {
                        List<SongDetail> songs = PhoneMediaControl.getInstance()
                                .loadMusicList(context, id, PhoneMediaControl.SongsLoadFor.ARTIST, "");

                        return new AsyncTaskResult<>(
                                new Playlist.Builder()
                                        .setId(id)
                                        .setName(name)
                                        .setSongs(songs)
                                        .build());
                    }
                };

            case ALBUM:
                return new AbstractAsyncTask<Playlist>(listener) {
                    @Override
                    protected AsyncTaskResult<Playlist> doInBackground(Void... params) {
                        List<SongDetail> songs = PhoneMediaControl.getInstance()
                                .loadMusicList(context, id, PhoneMediaControl.SongsLoadFor.ALBUM, "");

                        return new AsyncTaskResult<>(
                                new Playlist.Builder()
                                        .setId(id)
                                        .setName(name)
                                        .setSongs(songs)
                                        .build());
                    }
                };

            default:
                throw new IllegalArgumentException("Default case reached!");
        }
    }
}
