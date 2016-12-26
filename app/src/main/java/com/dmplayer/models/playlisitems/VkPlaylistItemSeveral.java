package com.dmplayer.models.playlisitems;

import com.dmplayer.models.PlaylistItem;

public class VkPlaylistItemSeveral extends PlaylistItem {
    private final VkPlaylistCategorySeveral category;

    public VkPlaylistItemSeveral(long id, String name, String details, int imageResourceId,
                                     VkPlaylistCategorySeveral category) {
        super(id, name, details, imageResourceId);

        this.category = category;
    }

    public VkPlaylistItemSeveral(PlaylistItem playlistItem,
                                 VkPlaylistCategorySeveral category) {
        this(playlistItem.getId(), playlistItem.getName(), playlistItem.getDetails(),
                playlistItem.getImageResourceId(), category);
    }

    public VkPlaylistCategorySeveral getCategory() {
        return category;
    }
}
