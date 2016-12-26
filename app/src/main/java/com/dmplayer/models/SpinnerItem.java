package com.dmplayer.models;


public class SpinnerItem {
    private String title;
    private int iconId;

    public SpinnerItem(int iconId, String title) {
        this.iconId = iconId;
        this.title = title;
    }

    public int getIconId() {
        return iconId;
    }

    public String getTitle() {
        return title;
    }
}
