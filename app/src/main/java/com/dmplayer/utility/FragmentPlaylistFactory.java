package com.dmplayer.utility;

import com.dmplayer.fragments.FragmentPlaylist;
import com.dmplayer.models.PlaylistItem;
import com.dmplayer.models.playlisitems.DefaultPlaylistItemSingle;
import com.dmplayer.models.playlisitems.VkPlaylistItemSingle;

public class FragmentPlaylistFactory {
    public FragmentPlaylist getFragmentPlaylist(PlaylistItem item) {
        FragmentPlaylist f = null;
        if (item instanceof DefaultPlaylistItemSingle) {
            DefaultPlaylistItemSingle itemDefault = (DefaultPlaylistItemSingle) item;

            f = FragmentPlaylist.newInstance(itemDefault.getCategory(),
                    itemDefault.getId(), itemDefault.getName());
        } else if (item instanceof VkPlaylistItemSingle) {
            VkPlaylistItemSingle itemVk = (VkPlaylistItemSingle) item;

            f = FragmentPlaylist.newInstance(itemVk.getCategory(),
                    itemVk.getId(), itemVk.getName());
        }
        return f;
    }
}