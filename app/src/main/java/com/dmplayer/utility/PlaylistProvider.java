package com.dmplayer.utility;

import android.content.Context;

import com.dmplayer.R;
import com.dmplayer.db.LocalPlaylistTablesHelper;
import com.dmplayer.models.Playlist;
import com.dmplayer.models.PlaylistItem;
import com.dmplayer.models.playlisitems.DefaultPlaylistCategorySeveral;
import com.dmplayer.models.playlisitems.DefaultPlaylistCategorySingle;
import com.dmplayer.models.playlisitems.DefaultPlaylistItemSeveral;
import com.dmplayer.models.playlisitems.DefaultPlaylistItemSingle;
import com.dmplayer.models.playlisitems.VkPlaylistCategorySeveral;
import com.dmplayer.models.playlisitems.VkPlaylistCategorySingle;
import com.dmplayer.models.playlisitems.VkPlaylistItemSeveral;
import com.dmplayer.models.playlisitems.VkPlaylistItemSingle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PlaylistProvider {
    private static final String TAG = PlaylistProvider.class.getSimpleName();

    private PlaylistProvider() {
        throw new AssertionError();
    }

    public static List<PlaylistItem> getLocalPlaylists(Context context) {
        List<Playlist> localPlaylists = LocalPlaylistTablesHelper.getInstance(context)
                .getLocalPlaylistsEmpty();
        List<PlaylistItem> temp = new ArrayList<>();
        for(Playlist playlist : localPlaylists) {
            temp.add(new DefaultPlaylistItemSingle(playlist.getId(), playlist.getName(), PlaylistItem.NO_DETAILS,
                    R.drawable.ic_play, DefaultPlaylistCategorySingle.LOCAL));
        }

        return temp;
    }

    public static List<PlaylistItem> getDefaultPlaylists() {
        return new ArrayList<>(Arrays.asList(
                new DefaultPlaylistItemSingle(PlaylistItem.NO_ID, "ALL songs", PlaylistItem.NO_DETAILS,
                        R.drawable.ic_play, DefaultPlaylistCategorySingle.ALL_SONGS),
                new DefaultPlaylistItemSingle(PlaylistItem.NO_ID, "Most played", PlaylistItem.NO_DETAILS,
                        R.drawable.ic_play, DefaultPlaylistCategorySingle.MOST_PLAYED),
                new DefaultPlaylistItemSeveral(PlaylistItem.NO_ID, "Albums", PlaylistItem.NO_DETAILS,
                        R.drawable.ic_play, DefaultPlaylistCategorySeveral.ALBUMS),
                new DefaultPlaylistItemSeveral(PlaylistItem.NO_ID, "Artists", PlaylistItem.NO_DETAILS,
                        R.drawable.ic_play, DefaultPlaylistCategorySeveral.ARTISTS),
                new DefaultPlaylistItemSeveral(PlaylistItem.NO_ID, "Genres", PlaylistItem.NO_DETAILS,
                        R.drawable.ic_play, DefaultPlaylistCategorySeveral.GENRES)
        ));
    }

    public static List<PlaylistItem> getVkPlaylists() {
        return new ArrayList<>(Arrays.asList(
                new VkPlaylistItemSingle(PlaylistItem.NO_ID, "My music", PlaylistItem.NO_DETAILS,
                        R.drawable.ic_play, VkPlaylistCategorySingle.MY_MUSIC),
                new VkPlaylistItemSingle(PlaylistItem.NO_ID, "Recommendations", PlaylistItem.NO_DETAILS,
                        R.drawable.ic_play, VkPlaylistCategorySingle.RECOMMENDATIONS),
                new VkPlaylistItemSeveral(PlaylistItem.NO_ID, "Popular", PlaylistItem.NO_DETAILS,
                        R.drawable.ic_play, VkPlaylistCategorySeveral.POPULAR),
                new VkPlaylistItemSeveral(PlaylistItem.NO_ID, "My albums", PlaylistItem.NO_DETAILS,
                        R.drawable.ic_play, VkPlaylistCategorySeveral.MY_ALBUMS)
        ));
    }
}
