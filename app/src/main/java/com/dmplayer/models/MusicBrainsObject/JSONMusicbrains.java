
package com.dmplayer.models.MusicBrainsObject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class JSONMusicbrains {

    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("offset")
    @Expose
    private int offset;
    @SerializedName("artists")
    @Expose
    private List<Artist> artists = new ArrayList<Artist>();

    /**
     * 
     * @return
     *     The created
     */
    public String getCreated() {
        return created;
    }

    /**
     * 
     * @param created
     *     The created
     */
    public void setCreated(String created) {
        this.created = created;
    }

    /**
     * 
     * @return
     *     The count
     */
    public int getCount() {
        return count;
    }

    /**
     * 
     * @param count
     *     The count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * 
     * @return
     *     The offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * 
     * @param offset
     *     The offset
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * 
     * @return
     *     The artists
     */
    public List<Artist> getArtists() {
        return artists;
    }

    /**
     * 
     * @param artists
     *     The artists
     */
    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

}
