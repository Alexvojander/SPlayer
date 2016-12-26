
package com.dmplayer.models.MusicBrainsObject;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class LifeSpan {

    @SerializedName("begin")
    @Expose
    private String begin;
    @SerializedName("ended")
    @Expose
    private Object ended;

    /**
     * 
     * @return
     *     The begin
     */
    public String getBegin() {
        return begin;
    }

    /**
     * 
     * @param begin
     *     The begin
     */
    public void setBegin(String begin) {
        this.begin = begin;
    }

    /**
     * 
     * @return
     *     The ended
     */
    public Object getEnded() {
        return ended;
    }

    /**
     * 
     * @param ended
     *     The ended
     */
    public void setEnded(Object ended) {
        this.ended = ended;
    }

}
