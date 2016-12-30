package com.dmplayer.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dmplayer.R;
import com.dmplayer.activities.DMPlayerBaseActivity;
import com.dmplayer.utility.RemotePlaylistServer;
import com.dmplayer.utility.asynctaskabstraction.TaskStateListener;
import com.dmplayer.manager.MediaController;
import com.dmplayer.models.Playlist;
import com.dmplayer.models.SongDetail;
import com.dmplayer.models.playlisitems.DefaultPlaylistCategorySingle;
import com.dmplayer.models.playlisitems.VkPlaylistCategorySingle;
import com.dmplayer.utility.DMPlayerUtility;
import com.dmplayer.utility.DefaultPlaylistTaskFactory;
import com.dmplayer.utility.VkPlaylistTaskFactory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import fi.iki.elonen.NanoHTTPD;

public class FragmentPlaylist extends BaseFragment {

    @BindView(R.id.recycle_songs) RecyclerView listOfSongs;
    @BindView(R.id.progress_playlist) ProgressBar progressBar;

    @BindView(R.id.error_layout) LinearLayout errorLayout;
    @BindView(R.id.error_info) TextView errorInfo;
    @BindView(R.id.error_reload) TextView errorReload;

    @BindView(R.id.fab_remote_playlist) FloatingActionButton buttonRemotePlaylist;

    private List<SongDetail> songList = new ArrayList<>();
    private SongListAdapter songAdapter = new SongListAdapter(getActivity(), songList);

    NanoHTTPD server;

    private static final String ARG_TYPE = "FragmentPlaylist_type";
    private static final String ARG_CATEGORY = "FragmentPlaylist_category";
    private static final String ARG_ID = "FragmentPlaylist_id";
    private static final String ARG_NAME = "FragmentPlaylist_name";

    private static final int TYPE_DEFAULT = 0;
    private static final int TYPE_VK = 1;

    private static final String TAG = FragmentPlaylist.class.getSimpleName();

    public static FragmentPlaylist newInstance(DefaultPlaylistCategorySingle category, long id,
                                               String name) {
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, TYPE_DEFAULT);
        args.putInt(ARG_CATEGORY, category.ordinal());
        args.putLong(ARG_ID, id);
        args.putString(ARG_NAME, name);

        FragmentPlaylist f = new FragmentPlaylist();
        f.setArguments(args);

        return f;
    }

    public static FragmentPlaylist newInstance(VkPlaylistCategorySingle category, long id,
                                               String name) {
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, TYPE_VK);
        args.putInt(ARG_CATEGORY, category.ordinal());
        args.putLong(ARG_ID, id);
        args.putString(ARG_NAME, name);

        FragmentPlaylist f = new FragmentPlaylist();
        f.setArguments(args);

        return f;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        loadPlaylist();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_playlist;
    }

    private void init() {
        listOfSongs.setLayoutManager(new LinearLayoutManager(getActivity()));
        listOfSongs.setAdapter(songAdapter);
    }

    @OnClick(R.id.error_reload)
    public void reloadPlaylist() {
        errorLayout.setVisibility(View.GONE);
        loadPlaylist();
    }

    @OnClick(R.id.fab_remote_playlist)
    public void switchRemotePlaylist() {
        if (server == null) {
            server = new RemotePlaylistServer(getActivity(), 8080);
        }

        if (server.isAlive()) {
            server.stop();
        } else {
            try {
                server.start();
            } catch (IOException e) {
                Log.e(TAG, "A problem with starting playlist server", e);
            }
        }
    }

    private void loadPlaylist() {
        TaskStateListener<Playlist> listener = new TaskStateListener<Playlist>() {
                @Override
                public void onLoadingStarted() {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingSuccessful(Playlist result) {
                    songList.clear();
                    songList.addAll(result.getSongs());
                    songAdapter.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    progressBar.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);

                    errorInfo.setText("");
                    errorInfo.append(getString(R.string.error) + "\n");
                    errorInfo.append(getString(R.string.check_connection));
                }
        };

        int type = getArguments().getInt(ARG_TYPE);
        int category = getArguments().getInt(ARG_CATEGORY);
        long id = getArguments().getLong(ARG_ID);
        String name = getArguments().getString(ARG_NAME);

        //TODO:think about strategy pattern
        switch (type) {
            case TYPE_DEFAULT:
                loadDefaultPlaylist(category, id, name, listener);
                break;

            case TYPE_VK:
                loadVkPlaylist(category, id, name, listener);
                break;

            default:
                throw new IllegalArgumentException("Default statement reached!");
        }
    }

    private void loadDefaultPlaylist(int category, long id, String name, TaskStateListener<Playlist> l) {
        DefaultPlaylistCategorySingle c = DefaultPlaylistCategorySingle.values()[category];

        DefaultPlaylistTaskFactory factory = new DefaultPlaylistTaskFactory(getActivity(), l);
        factory.getTask(c, id, name).execute();
    }

    private void loadVkPlaylist(int category, long id, String name, TaskStateListener<Playlist> l) {
        VkPlaylistCategorySingle c = VkPlaylistCategorySingle.values()[category];

        VkPlaylistTaskFactory factory = new VkPlaylistTaskFactory(getActivity(), l);
        factory.getTask(c, id, name).execute();
    }

    private class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder> {

        List<SongDetail> songList;

        private DisplayImageOptions options;
        private ImageLoader imageLoader = ImageLoader.getInstance();

        public SongListAdapter(Context context, List<SongDetail> songList) {
            this.songList = songList;
            this.options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.bg_default_album_art)
                    .showImageForEmptyUri(R.drawable.bg_default_album_art)
                    .showImageOnFail(R.drawable.bg_default_album_art)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            SongDetail currentSong = songList.get(position);

            String audioDuration = "";
            try {
                audioDuration = DMPlayerUtility.getAudioDuration(Long.parseLong(currentSong.getDuration()));
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error getting audio duration:", e);
            }

            holder.artistAndDuration.setText((audioDuration.isEmpty() ? "" : audioDuration + " | ") + currentSong.getArtist());
            holder.songName.setText(currentSong.getTitle());
            String contentURI = "content://media/external/audio/media/" + currentSong.getId() + "/albumart";
            imageLoader.displayImage(contentURI, holder.songImage, options);
        }

        @Override
        public int getItemCount() {
            return (songList != null) ? songList.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView songName;
            ImageView songImage;
            ImageView menuImage;
            TextView artistAndDuration;

            public ViewHolder(View itemView) {
                super(itemView);
                songName = (TextView) itemView.findViewById(R.id.song_name);
                artistAndDuration = (TextView) itemView.findViewById(R.id.song_details);
                songImage = (ImageView) itemView.findViewById(R.id.song_icon_art);
                //TODO: problems with menu icon
                menuImage = (ImageView) itemView.findViewById(R.id.song_icon_option_more);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                SongDetail song = songList.get(getAdapterPosition());
                song.audioProgress = 0.0f;
                song.audioProgressSec = 0;
                ((DMPlayerBaseActivity) getActivity()).loadSongsDetails(song);

                if (MediaController.getInstance().isPlayingAudio(song) && !MediaController.getInstance().isAudioPaused()) {
                    MediaController.getInstance().pauseAudio(song);
                } else {
                    //TODO: 3rd parameter is playlist type, it used to retrieve song when activity closes
                    MediaController.getInstance().setPlaylist(songList, song, -1, -1);
                }
            }
        }
    }
}
