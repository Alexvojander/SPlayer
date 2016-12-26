package com.dmplayer.utility;

import com.dmplayer.dialogs.SeveralPlaylistDialog;
import com.dmplayer.models.PlaylistItem;
import com.dmplayer.models.playlisitems.DefaultPlaylistItemSeveral;
import com.dmplayer.models.playlisitems.VkPlaylistItemSeveral;

public class SeveralPlaylistDialogFactory {

    public SeveralPlaylistDialog getSeveralPlaylistDialog(PlaylistItem item) {
        SeveralPlaylistDialog dialog = null;
        if (item instanceof DefaultPlaylistItemSeveral) {
            DefaultPlaylistItemSeveral itemDefault = (DefaultPlaylistItemSeveral) item;

            dialog = SeveralPlaylistDialog.newInstance(itemDefault.getCategory(), itemDefault.getName());
        } else if (item instanceof VkPlaylistItemSeveral) {
            VkPlaylistItemSeveral itemDefault = (VkPlaylistItemSeveral) item;

            dialog = SeveralPlaylistDialog.newInstance(itemDefault.getCategory(), itemDefault.getName());
        }
        return dialog;
    }
}
