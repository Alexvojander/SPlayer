package com.dmplayer.models.VkObjects;

import android.graphics.Bitmap;

import com.dmplayer.ui.externalprofile.ExternalProfileModel;

public class VkProfileModel implements ExternalProfileModel {
    private final Bitmap photo;
    private final String nickname;
    private final String songsCount;
    private final String albumsCount;

    public VkProfileModel(Bitmap photo, String nickname, String songsCount, String albumsCount) {
        this.photo = photo;
        this.nickname = nickname;
        this.songsCount = songsCount;
        this.albumsCount = albumsCount;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public String getNickname() {
        return nickname;
    }

    public String getSongsCount() {
        return songsCount;
    }

    public String getAlbumsCount() {
        return albumsCount;
    }

    public static class Builder {
        private Bitmap photo;
        private String nickname;
        private String songsCount;
        private String albumsCount;

        public Builder setPhoto(Bitmap photo) {
            this.photo = photo;

            return this;
        }

        public Builder setNickname(String nickname) {
            this.nickname = nickname;

            return this;
        }

        public void addNicknamePart(String part) {
            nickname = ((nickname == null) ? "" : nickname) + part;
        }

        public Builder setSongsCount(String songsCount) {
            this.songsCount = songsCount;

            return this;
        }

        public Builder setAlbumsCount(String albumsCount) {
            this.albumsCount = albumsCount;

            return this;
        }

        public VkProfileModel build() {
            return new VkProfileModel(photo, nickname, songsCount, albumsCount);
        }
    }
}
