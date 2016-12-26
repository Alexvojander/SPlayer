package com.dmplayer.dialogs;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dmplayer.R;
import com.dmplayer.utility.asynctaskabstraction.TaskStateListener;
import com.dmplayer.fragments.FragmentPlaylist;
import com.dmplayer.models.PlaylistItem;
import com.dmplayer.models.playlisitems.DefaultPlaylistCategorySeveral;
import com.dmplayer.models.playlisitems.DefaultPlaylistItemSingle;
import com.dmplayer.models.playlisitems.VkPlaylistCategorySeveral;
import com.dmplayer.models.playlisitems.VkPlaylistItemSingle;
import com.dmplayer.utility.DefaultMultiPlaylistTaskFactory;
import com.dmplayer.utility.VkMultiPlaylistTaskFactory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SeveralPlaylistDialog extends BaseDialogFragment {
    @BindView(R.id.recycler_playlists)
    RecyclerView listOfPlaylists;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.error_layout)
    LinearLayout errorLayout;
    @BindView(R.id.error_info)
    TextView errorInfo;
    @BindView(R.id.error_reload)
    TextView errorReload;

    @BindView(R.id.button_close)
    ImageView buttonClose;

    @BindView(R.id.title)
    TextView textTitle;

    PlaylistListAdapter playlistsAdapter;

    private static final String ARG_TYPE = "SeveralPlaylistDialog_type";
    private static final String ARG_CATEGORY = "SeveralPlaylistDialog_category";
    private static final String ARG_NAME = "SeveralPlaylistDialog_name";

    private static final int TYPE_DEFAULT = 0;
    private static final int TYPE_VK = 1;

    public static SeveralPlaylistDialog newInstance(DefaultPlaylistCategorySeveral category,
                                                    String name) {
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, TYPE_DEFAULT);
        args.putInt(ARG_CATEGORY, category.ordinal());
        args.putString(ARG_NAME, name);

        SeveralPlaylistDialog d = new SeveralPlaylistDialog();
        d.setArguments(args);

        return d;
    }

    public static SeveralPlaylistDialog newInstance(VkPlaylistCategorySeveral category,
                                                    String name) {
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, TYPE_VK);
        args.putInt(ARG_CATEGORY, category.ordinal());
        args.putString(ARG_NAME, name);

        SeveralPlaylistDialog d = new SeveralPlaylistDialog();
        d.setArguments(args);

        return d;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        loadPlaylist();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_playlistseveral;
    }

    private void init() {
        listOfPlaylists.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        errorReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.GONE);
                listOfPlaylists.setVisibility(View.VISIBLE);
                loadPlaylist();
            }
        });

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        String title = getArguments().getString(ARG_NAME);
        textTitle.setText(title);
    }

    private void loadPlaylist() {
        final int type = getArguments().getInt(ARG_TYPE);

        TaskStateListener<List<PlaylistItem>> listener =
                new TaskStateListener<List<PlaylistItem>>() {
                    @Override
                    public void onLoadingStarted() {
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingSuccessful(List<PlaylistItem> result) {
                        playlistsAdapter = new SeveralPlaylistDialog.PlaylistListAdapter(getActivity(), type,
                                result);
                        listOfPlaylists.setAdapter(playlistsAdapter);

                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        listOfPlaylists.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);

                        errorLayout.setVisibility(View.VISIBLE);

                        errorInfo.setText("");
                        errorInfo.append(getString(R.string.error) + "\n");
                        errorInfo.append(getString(R.string.check_connection));
                    }
                };

        int category = getArguments().getInt(ARG_CATEGORY);

        switch (type) {
            case TYPE_DEFAULT:
                loadPlaylistsFromDefault(category, listener);
                break;
            case TYPE_VK:
                loadPlaylistsFromVk(category, listener);
                break;
            default:
                throw new IllegalArgumentException("Default statement reached!");
        }
    }

    private void loadPlaylistsFromDefault(int category, TaskStateListener<List<PlaylistItem>> l) {
        DefaultPlaylistCategorySeveral c = DefaultPlaylistCategorySeveral.values()[category];

        DefaultMultiPlaylistTaskFactory factory = new DefaultMultiPlaylistTaskFactory(getActivity(), l);
        factory.getTask(c).execute();
    }

    private void loadPlaylistsFromVk(int category, TaskStateListener<List<PlaylistItem>> l) {
        VkPlaylistCategorySeveral c = VkPlaylistCategorySeveral.values()[category];

        VkMultiPlaylistTaskFactory factory = new VkMultiPlaylistTaskFactory(getActivity(), l);
        factory.getTask(c).execute();
    }

    protected class PlaylistListAdapter extends RecyclerView.Adapter<PlaylistListAdapter.ViewHolder> {
        private List<? extends PlaylistItem> items;
        private int type;

        private String unknownPlaylistName;

        private ImageLoader imageLoader = ImageLoader.getInstance();
        private DisplayImageOptions options;

        protected PlaylistListAdapter(Context context, int type,
                                      List<? extends PlaylistItem> items) {
            this.items = items;
            this.type = type;

            unknownPlaylistName = context.getString(R.string.unknown_playlist);

            options = new DisplayImageOptions.Builder()
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
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_grid_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (type) {
                case TYPE_DEFAULT:
                    bindViewForDefault(holder, position);
                    break;
                case TYPE_VK:
                    bindViewForVk(holder, position);
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        private void bindViewForDefault(ViewHolder holder, int position) {
            DefaultPlaylistItemSingle currentItem =
                    (DefaultPlaylistItemSingle) items.get(position);

            String playlistName = currentItem.getName();
            String contentURI = "content://media/external/audio/albumart/" + currentItem.getId();

            if ((playlistName == null) || playlistName.equals(MediaStore.UNKNOWN_STRING)) {
                playlistName = unknownPlaylistName;
            }

            holder.title.setText(playlistName);
            imageLoader.displayImage(contentURI, holder.icon, options);
        }

        private void bindViewForVk(ViewHolder holder, int position) {
            VkPlaylistItemSingle currentItem = (VkPlaylistItemSingle) items.get(position);

            holder.title.setText(currentItem.getName());
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.title)
            TextView title;
            @BindView(R.id.details)
            TextView details;
            @BindView(R.id.icon)
            ImageView icon;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                init();
            }

            private void init() {
                icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            @OnClick
            public void showSinglePlaylist() {
                dismiss();

                FragmentPlaylist f;
                switch(type) {
                    case TYPE_DEFAULT:
                        DefaultPlaylistItemSingle defaultItem =
                                (DefaultPlaylistItemSingle) items.get(getAdapterPosition());

                        f = FragmentPlaylist
                                .newInstance(defaultItem.getCategory(), defaultItem.getId(), defaultItem.getName());
                        break;
                    case TYPE_VK:
                        VkPlaylistItemSingle vkItem =
                                (VkPlaylistItemSingle) items.get(getAdapterPosition());

                        f = FragmentPlaylist
                                .newInstance(vkItem.getCategory(), vkItem.getId(), vkItem.getName());
                        break;
                    default:
                        throw new IllegalArgumentException("Default statement reached!");
                }

                FragmentManager fm = getActivity().getFragmentManager();

                fm.beginTransaction()
                        .replace(R.id.fragment, f)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }
}
