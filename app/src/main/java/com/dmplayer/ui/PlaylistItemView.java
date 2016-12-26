package com.dmplayer.ui;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dmplayer.R;
import com.dmplayer.models.PlaylistItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaylistItemView extends RelativeLayout {
    @BindView(R.id.expandable_item_playlist_button_close)
    ImageView closeButton;

    public PlaylistItemView(Context context, PlaylistItem playlistItem) {
        super(context);
        init(context, playlistItem);
    }

    private void init(Context context, PlaylistItem playlistItem) {
        inflate(context, R.layout.expandable_item_playlist, this);
        ButterKnife.bind(this);

        ((TextView) ButterKnife.findById(this, R.id.name)).setText(playlistItem.getName());
        ((TextView) ButterKnife.findById(this, R.id.details)).setText(playlistItem.getDetails());
        ((ImageView) ButterKnife.findById(this, R.id.icon)).setImageResource(playlistItem.getImageResourceId());
    }

    public void setCloseButtonVisible() {
        closeButton.setVisibility(VISIBLE);
    }

    public void setCloseButtonOnClickListener(OnClickListener l) {
        closeButton.setOnClickListener(l);
    }
}