package com.dmplayer.models.VkObjects;

import com.dmplayer.models.SongDetail;
import com.dmplayer.models.VkObjects.VkAudioObject;

import java.io.IOException;

import retrofit2.Converter;

public class VkToSongDetailConverter implements Converter<VkAudioObject, SongDetail> {
    @Override
    public SongDetail convert(VkAudioObject value) throws IOException {
        final int ID = (value.getId() == null)? -1: Integer.valueOf(value.getId());
        final int ALBUM_ID = (value.getAlbum_id() == null)? -1: Integer.valueOf(value.getAlbum_id());
        final String ARTIST = (value.getArtist() == null)? "": value.getArtist();
        final String TITLE = (value.getTitle() == null)? "": value.getTitle();
        final String PATH = (value.getUrl() == null)? "": value.getUrl();
        final String DISPLAY_NAME = ARTIST + " - " + TITLE;
        final String DURATION = (value.getDuration() == null)? "":
                String.valueOf(Long.valueOf(value.getDuration()) * 1000);

        return new SongDetail(ID, ALBUM_ID, ARTIST, TITLE, PATH, DISPLAY_NAME, DURATION);
    }
}
