package com.dmplayer.models.playlisitems;

import com.dmplayer.models.PlaylistItem;

public class VkPlaylistItemSingle extends PlaylistItem {
    private final VkPlaylistCategorySingle category;

    public VkPlaylistItemSingle(long id, String name, String details, int imageResourceId,
                                VkPlaylistCategorySingle category) {
        super(id, name, details, imageResourceId);

        this.category = category;
    }

    public VkPlaylistItemSingle(PlaylistItem playlistItem,
                                VkPlaylistCategorySingle category) {
        this(playlistItem.getId(), playlistItem.getName(), playlistItem.getDetails(),
                playlistItem.getImageResourceId(), category);
    }

    public VkPlaylistCategorySingle getCategory() {
        return category;
    }
}
