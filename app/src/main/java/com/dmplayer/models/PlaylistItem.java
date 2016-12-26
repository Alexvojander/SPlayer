package com.dmplayer.models;

public class PlaylistItem {
    public static final long NO_ID = -1;
    public static final String NO_NAME = "<NO_NAME>";
    public static final String NO_DETAILS = "<NO_DETAILS>";
    public static final int NO_IMAGE_RESOURCE = -1;

    private final long id;
    private final String name;
    private final String details;
    private final int imageResourceId;

    public PlaylistItem(long id, String name, String details, int imageResourceId) {
        this.id = id;
        this.name = name;
        this.details = details;
        this.imageResourceId = imageResourceId;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public static class Builder {
        private long id = NO_ID;
        private String name = NO_NAME;
        private String details = NO_DETAILS;
        private int imageResourceId = NO_IMAGE_RESOURCE;

        public Builder setId(long id) {
            this.id = id;

            return this;
        }

        public Builder setName(String name) {
            this.name = name;

            return this;
        }

        public Builder setDetails(String details) {
            this.details = details;

            return this;
        }

        public Builder setImageResourceId(int imageResourceId) {
            this.imageResourceId = imageResourceId;

            return this;
        }

        public PlaylistItem build() {
            return new PlaylistItem(id, name, details, imageResourceId);
        }
    }
}