package com.dmplayer.models.playlisitems;

import com.dmplayer.models.PlaylistItem;

public class DefaultPlaylistItemSeveral extends PlaylistItem {
    private final DefaultPlaylistCategorySeveral category;

    public DefaultPlaylistItemSeveral(long id, String name, String details, int imageResourceId,
                                      DefaultPlaylistCategorySeveral category) {
        super(id, name, details, imageResourceId);

        this.category = category;
    }

    public DefaultPlaylistItemSeveral(PlaylistItem playlistItem,
                                      DefaultPlaylistCategorySeveral category) {
        this(playlistItem.getId(), playlistItem.getName(), playlistItem.getDetails(),
                playlistItem.getImageResourceId(), category);
    }

    public DefaultPlaylistCategorySeveral getCategory() {
        return category;
    }
}
