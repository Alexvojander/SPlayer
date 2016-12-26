package com.dmplayer.models.playlisitems;

import com.dmplayer.models.PlaylistItem;

public class DefaultPlaylistItemSingle extends PlaylistItem {
    private final DefaultPlaylistCategorySingle category;

    public DefaultPlaylistItemSingle(long id, String name, String details, int imageResourceId,
                                     DefaultPlaylistCategorySingle category) {
        super(id, name, details, imageResourceId);

        this.category = category;
    }

    public DefaultPlaylistItemSingle(PlaylistItem playlistItem,
                                     DefaultPlaylistCategorySingle category) {
        this(playlistItem.getId(), playlistItem.getName(), playlistItem.getDetails(),
                playlistItem.getImageResourceId(), category);
    }

    public DefaultPlaylistCategorySingle getCategory() {
        return category;
    }
}
