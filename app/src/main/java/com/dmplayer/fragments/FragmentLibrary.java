package com.dmplayer.fragments;

import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dmplayer.R;
import com.dmplayer.db.LocalPlaylistTablesHelper;
import com.dmplayer.dialogs.OnWorkDone;
import com.dmplayer.dialogs.ProfileDialog;
import com.dmplayer.dialogs.SeveralPlaylistDialog;
import com.dmplayer.utility.vk.VkProfileHelper;
import com.dmplayer.models.PlaylistItem;
import com.dmplayer.models.playlisitems.DefaultPlaylistItemSeveral;
import com.dmplayer.models.playlisitems.DefaultPlaylistItemSingle;
import com.dmplayer.models.playlisitems.VkPlaylistItemSeveral;
import com.dmplayer.models.playlisitems.VkPlaylistItemSingle;
import com.dmplayer.ui.ExpandableLayout;
import com.dmplayer.ui.ExpandableLayoutExternalAccount;
import com.dmplayer.ui.ExpandableLayoutManager;
import com.dmplayer.ui.PlaylistItemView;
import com.dmplayer.utility.FragmentPlaylistFactory;
import com.dmplayer.utility.PlaylistProvider;
import com.dmplayer.utility.SeveralPlaylistDialogFactory;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class FragmentLibrary extends BaseFragment {
    private static final String TAG = "FragmentLibrary";

    private ExpandableLayoutManager expandableManager;

    @BindView(R.id.local_playlists)
    ExpandableLayout expandableLocal;
    @BindView(R.id.default_playlists)
    ExpandableLayout expandableDefault;
    @BindView(R.id.vk_playlists)
    ExpandableLayoutExternalAccount expandableVk;

    @BindView(R.id.button_add_local_playlist)
    ImageView buttonAddLocalPlaylist;

    private List<PlaylistItem> localPlaylists;
    private List<PlaylistItem> defaultPlaylists;
    private List<PlaylistItem> vkPlaylists;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_library;
    }

    private void init() {
        expandableLocal.setOnExpandListener(new ExpandableLayout.OnExpandListener() {
            @Override
            public void OnExpand(ExpandableLayout v) {
                expandableManager.collapseOthers(v);

                setupLocalPlaylists();
            }
        });

        expandableDefault.setOnExpandListener(new ExpandableLayout.OnExpandListener() {
            @Override
            public void OnExpand(ExpandableLayout v) {
                expandableManager.collapseOthers(v);

                setupDefaultPlaylists();
            }
        });

        expandableVk.setOnExpandListener(new ExpandableLayout.OnExpandListener() {
            @Override
            public void OnExpand(ExpandableLayout v) {
                expandableManager.collapseOthers(v);

                setupVkPlaylists();
            }
        });
        checkVkLogged();

        expandableManager = new ExpandableLayoutManager();
        expandableManager.register(expandableLocal);
        expandableManager.register(expandableDefault);
        expandableManager.register(expandableVk);
    }

    private void checkVkLogged() {
        VkProfileHelper vkHelper = new VkProfileHelper.Builder(getActivity()).build();
        if (!vkHelper.isLogged()) {
            expandableVk.setMessageLayout();

            TextView infoText = (TextView) expandableVk.findViewById(R.id.title_not_logged);
            infoText.setText(getString(R.string.not_logged, "Vk"));

            ImageView toProfile = (ImageView) expandableVk.findViewById(R.id.button_to_profile);
            toProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fragmentManager = getActivity().getFragmentManager();

                    final ProfileDialog dialog = new ProfileDialog();
                    dialog.setOnWorkDone(new OnWorkDone() {
                        @Override
                        public void onAgree() {
                            checkVkLogged();
                        }

                        @Override
                        public void onRefuse() {}
                    });
                    dialog.show(fragmentManager, "fragment_profile");
                }
            });
        } else {
            expandableVk.setUsualLayout();
        }
    }

    private void setupLocalPlaylists() {
        localPlaylists = PlaylistProvider.getLocalPlaylists(getActivity());

        if (expandableLocal.getContentAmount() <= 0) {
            for (final PlaylistItem playlistItem : localPlaylists) {
                PlaylistItemView itemView = new PlaylistItemView(getActivity(), playlistItem);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        replaceFragmentToPlaylist(playlistItem);
                    }
                });
                itemView.setCloseButtonVisible();
                itemView.setCloseButtonOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LocalPlaylistTablesHelper.getInstance(getActivity())
                                .deletePlaylist(playlistItem.getId());
                        prepareLocalPlaylists();
                    }
                });
                expandableLocal.addContent(itemView);
            }
        }
    }

    private void prepareLocalPlaylists() {
        expandableLocal.eraseContent();

        localPlaylists = PlaylistProvider.getLocalPlaylists(getActivity());
        for (final PlaylistItem playlistItem : localPlaylists) {
            PlaylistItemView itemView = new PlaylistItemView(getActivity(), playlistItem);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    replaceFragmentToPlaylist(playlistItem);
                }
            });
            itemView.setCloseButtonVisible();
            itemView.setCloseButtonOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LocalPlaylistTablesHelper.getInstance(getActivity())
                            .deletePlaylist(playlistItem.getId());
                }
            });
            expandableLocal.addContent(itemView);
        }
    }

    private void setupDefaultPlaylists() {
        if (defaultPlaylists == null) {
            defaultPlaylists = PlaylistProvider.getDefaultPlaylists();
        }

        if (expandableDefault.getContentAmount() <= 0) {
            for (final PlaylistItem playlistItem : defaultPlaylists) {
                PlaylistItemView itemView = new PlaylistItemView(getActivity(), playlistItem);

                if (playlistItem instanceof DefaultPlaylistItemSingle) {
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            replaceFragmentToPlaylist(playlistItem);
                        }
                    });
                }
                if (playlistItem instanceof DefaultPlaylistItemSeveral) {
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showSeveralPlaylistDialog(playlistItem);
                        }
                    });
                }
                expandableDefault.addContent(itemView);
            }
        }
    }

    private void setupVkPlaylists() {
        if (vkPlaylists == null) {
            vkPlaylists = PlaylistProvider.getVkPlaylists();
        }

        if (expandableVk.getContentAmount() <= 0) {
            for (final PlaylistItem playlistItem : vkPlaylists) {
                PlaylistItemView itemView = new PlaylistItemView(getActivity(), playlistItem);

                if (playlistItem instanceof VkPlaylistItemSingle) {
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            replaceFragmentToPlaylist(playlistItem);
                        }
                    });
                }
                if (playlistItem instanceof VkPlaylistItemSeveral) {
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showSeveralPlaylistDialog(playlistItem);
                        }
                    });
                }

                expandableVk.addContent(itemView);
            }
        }
    }

    @OnClick({R.id.local_playlists, R.id.default_playlists, R.id.vk_playlists})
    public void changeExpandableState(RelativeLayout v) {
        try {
            ExpandableLayout expandable = (ExpandableLayout) v.getParent();

            if(expandable.isExpanded()) {
                expandable.collapse();
            } else {
                expandable.expand();
            }
        } catch (ClassCastException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    @OnClick(R.id.button_add_local_playlist)
    public void showMusicChooserFragment() {
        FragmentManager fm = getFragmentManager();

        FragmentMusicChooser f = new FragmentMusicChooser();

        fm.beginTransaction()
                .replace(R.id.fragment, f)
                .addToBackStack(null)
                .commit();
    }

    private void replaceFragmentToPlaylist(PlaylistItem item) {
        FragmentManager fm = getFragmentManager();

        FragmentPlaylist f = new FragmentPlaylistFactory().getFragmentPlaylist(item);

        fm.beginTransaction()
                .replace(R.id.fragment, f)
                .addToBackStack(null)
                .commit();
    }

    private void showSeveralPlaylistDialog(PlaylistItem item) {
        FragmentManager fm = getActivity().getFragmentManager();

        SeveralPlaylistDialog dialog = new SeveralPlaylistDialogFactory()
                .getSeveralPlaylistDialog(item);

        dialog.show(fm, "fragment_playlistseveral");
    }
}