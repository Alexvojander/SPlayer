package com.dmplayer.models.VkObjects;

public class VkAudioObject {
    private String id;
    private String owner_id;
    private String artist;
    private String title;
    private String duration;
    private String url;
    private String lyrics_id;
    private String album_id;
    private String genre_id;
    private String date;
    private String no_search;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getOwner_id()
    {
        return owner_id;
    }

    public void setOwner_id(String owner_id)
    {
        this.owner_id = owner_id;
    }

    public String getArtist()
    {
        return artist;
    }

    public void setArtist(String artist)
    {
        this.artist = artist;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDuration()
    {
        return duration;
    }

    public void setDuration(String duration)
    {
        this.duration = duration;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getLyrics_id()
    {
        return lyrics_id;
    }

    public void setLyrics_id(String lyrics_id)
    {
        this.lyrics_id = lyrics_id;
    }

    public String getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public String getGenre_id()
    {
        return genre_id;
    }

    public void setGenre_id(String genre_id)
    {
        this.genre_id = genre_id;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getNo_search() {
        return no_search;
    }

    public void setNo_search(String no_search) {
        this.no_search = no_search;
    }


    @Override
    public String toString() {
        return "VkAudioObject [id = " + id +
                ", duration = " + duration +
                ", title = " + title +
                ", owner_id = " + owner_id +
                ", artist = " + artist +
                ", date = " + date +
                ", url = " + url +
                ", genre_id = " + genre_id +
                ", lyrics_id = " + lyrics_id + "]";
    }
}