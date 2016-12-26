package com.dmplayer.utility;

import android.content.Context;
import android.os.AsyncTask;

import com.dmplayer.utility.asynctaskabstraction.AbstractAsyncTask;
import com.dmplayer.utility.asynctaskabstraction.TaskStateListener;
import com.dmplayer.utility.vk.VkMusicHelper;
import com.dmplayer.models.AsyncTaskResult;
import com.dmplayer.models.PlaylistItem;
import com.dmplayer.models.playlisitems.VkPlaylistCategorySeveral;

import java.io.IOException;
import java.util.List;

public final class VkMultiPlaylistTaskFactory {
    private TaskStateListener<List<PlaylistItem>> listener;

    private VkMusicHelper musicLoader;

    public VkMultiPlaylistTaskFactory(Context context,
                                      TaskStateListener<List<PlaylistItem>> listener) {
        musicLoader = new VkMusicHelper.Builder(context).build();

        this.listener = listener;
    }

    public AsyncTask<Void, Void, AsyncTaskResult<List<PlaylistItem>>>
    getTask(final VkPlaylistCategorySeveral category) {
        switch (category) {
            case MY_ALBUMS:
                return new AbstractAsyncTask<List<PlaylistItem>>(listener) {
                    @Override
                    protected AsyncTaskResult<List<PlaylistItem>> doInBackground(Void... params) {
                        try {
                            return new AsyncTaskResult<>(musicLoader.loadUserAlbums());
                        } catch (IOException | NullPointerException e) {
                            return new AsyncTaskResult<>(e);
                        }
                    }
                };
            case POPULAR:
                return new AbstractAsyncTask<List<PlaylistItem>>(listener) {
                    @Override
                    protected AsyncTaskResult<List<PlaylistItem>> doInBackground(Void... params) {
                        return new AsyncTaskResult<>(musicLoader.loadPopularGenres());
                    }
                };
            default:
                throw new IllegalArgumentException("Default case reached!");
        }
    }
}
