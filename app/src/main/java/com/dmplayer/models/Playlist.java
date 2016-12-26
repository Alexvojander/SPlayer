package com.dmplayer.models;

import java.io.Serializable;
import java.util.List;

public class Playlist implements Serializable {

    private long id;
    private String name;
    private List<SongDetail> songs;

    private Playlist(long id, String name, List<SongDetail> songs) {
        this.id = id;
        this.name = name;
        this.songs = songs;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SongDetail> getSongs(){
        return songs;
    }

    public void setSongs(List<SongDetail> songs) {
        this.songs = songs;
    }

    public static class Builder {

        private long id;
        private String name;
        private List<SongDetail> songs;

        public Builder setId(long id) {
            this.id = id;

            return this;
        }

        public Builder setName(String name) {
            this.name = name;

            return this;
        }

        public Builder setSongs(List<SongDetail> songs) {
            this.songs = songs;

            return this;
        }

        public Playlist build() {
            return new Playlist(id, name, songs);
        }
    }
}
