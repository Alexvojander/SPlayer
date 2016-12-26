package com.dmplayer.utility;

import android.content.Context;
import android.os.AsyncTask;

import com.dmplayer.utility.asynctaskabstraction.AbstractAsyncTask;
import com.dmplayer.utility.asynctaskabstraction.TaskStateListener;
import com.dmplayer.utility.vk.VkMusicHelper;
import com.dmplayer.models.AsyncTaskResult;
import com.dmplayer.models.Playlist;
import com.dmplayer.models.playlisitems.VkPlaylistCategorySingle;

import java.io.IOException;

public final class VkPlaylistTaskFactory {
    private TaskStateListener<Playlist> listener;

    private VkMusicHelper musicLoader;

    public VkPlaylistTaskFactory(Context context, TaskStateListener<Playlist> listener) {
        musicLoader = new VkMusicHelper.Builder(context).build();

        this.listener = listener;
    }

    public AsyncTask<Void, Void, AsyncTaskResult<Playlist>>
    getTask(VkPlaylistCategorySingle category, final long id, final String name) {
        switch (category) {
            case MY_MUSIC:
                return new AbstractAsyncTask<Playlist>(listener) {
                    @Override
                    protected AsyncTaskResult<Playlist> doInBackground(Void... params) {
                        try {
                            return new AsyncTaskResult<>(
                                    new Playlist.Builder()
                                            .setId(id)
                                            .setName(name)
                                            .setSongs(musicLoader.loadAlbum(0, 100, -1))
                                            .build());
                        } catch (IOException | NullPointerException e) {
                            return new AsyncTaskResult<>(e);
                        }
                    }
                };
            case RECOMMENDATIONS:
                return new AbstractAsyncTask<Playlist>(listener) {
                    @Override
                    protected AsyncTaskResult<Playlist> doInBackground(Void... params) {
                        try {
                            return new AsyncTaskResult<>(
                                    new Playlist.Builder()
                                            .setId(id)
                                            .setName(name)
                                            .setSongs(musicLoader.loadRecommended(0, 100))
                                            .build());
                        } catch (IOException | NullPointerException e) {
                            return new AsyncTaskResult<>(e);
                        }
                    }
                };
            case GENRE:
                return new AbstractAsyncTask<Playlist>(listener) {
                    @Override
                    protected AsyncTaskResult<Playlist> doInBackground(Void... params) {
                        try {
                            return new AsyncTaskResult<>(
                                    new Playlist.Builder()
                                            .setId(id)
                                            .setName(name)
                                            .setSongs(musicLoader.loadPopular(0, 100, id))
                                            .build());
                        } catch (IOException | NullPointerException e) {
                            return new AsyncTaskResult<>(e);
                        }
                    }
                };
            case ALBUM:
                return new AbstractAsyncTask<Playlist>(listener) {
                    @Override
                    protected AsyncTaskResult<Playlist> doInBackground(Void... params) {
                        try {
                            return new AsyncTaskResult<>(
                                    new Playlist.Builder()
                                            .setId(id)
                                            .setName(name)
                                            .setSongs(musicLoader.loadAlbum(0, 100, id))
                                            .build());
                        } catch (IOException | NullPointerException e) {
                            return new AsyncTaskResult<>(e);
                        }
                    }
                };
            default:
                throw new IllegalArgumentException("Default case reached!");
        }
    }

}
