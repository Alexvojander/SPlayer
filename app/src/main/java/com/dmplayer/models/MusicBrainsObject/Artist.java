
package com.dmplayer.models.MusicBrainsObject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class Artist {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("score")
    @Expose
    private String score;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("sort-name")
    @Expose
    private String sortName;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("area")
    @Expose
    private Area area;
    @SerializedName("begin-area")
    @Expose
    private BeginArea beginArea;
    @SerializedName("disambiguation")
    @Expose
    private String disambiguation;
    @SerializedName("life-span")
    @Expose
    private LifeSpan lifeSpan;
    @SerializedName("tags")
    @Expose
    private List<Tag> tags = new ArrayList<Tag>();
    @SerializedName("aliases")
    @Expose
    private List<Alias> aliases = new ArrayList<Alias>();
    @SerializedName("gender")
    @Expose
    private String gender;

    /**
     * 
     * @return
     *     The id
     */
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The type
     */
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     * @return
     *     The score
     */
    public String getScore() {
        return score;
    }

    /**
     * 
     * @param score
     *     The score
     */
    public void setScore(String score) {
        this.score = score;
    }

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The sortName
     */
    public String getSortName() {
        return sortName;
    }

    /**
     * 
     * @param sortName
     *     The sort-name
     */
    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    /**
     * 
     * @return
     *     The country
     */
    public String getCountry() {
        return country;
    }

    /**
     * 
     * @param country
     *     The country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * 
     * @return
     *     The area
     */
    public Area getArea() {
        return area;
    }

    /**
     * 
     * @param area
     *     The area
     */
    public void setArea(Area area) {
        this.area = area;
    }

    /**
     * 
     * @return
     *     The beginArea
     */
    public BeginArea getBeginArea() {
        return beginArea;
    }

    /**
     * 
     * @param beginArea
     *     The begin-area
     */
    public void setBeginArea(BeginArea beginArea) {
        this.beginArea = beginArea;
    }

    /**
     * 
     * @return
     *     The disambiguation
     */
    public String getDisambiguation() {
        return disambiguation;
    }

    /**
     * 
     * @param disambiguation
     *     The disambiguation
     */
    public void setDisambiguation(String disambiguation) {
        this.disambiguation = disambiguation;
    }

    /**
     * 
     * @return
     *     The lifeSpan
     */
    public LifeSpan getLifeSpan() {
        return lifeSpan;
    }

    /**
     * 
     * @param lifeSpan
     *     The life-span
     */
    public void setLifeSpan(LifeSpan lifeSpan) {
        this.lifeSpan = lifeSpan;
    }

    /**
     * 
     * @return
     *     The tags
     */
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * 
     * @param tags
     *     The tags
     */
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    /**
     * 
     * @return
     *     The aliases
     */
    public List<Alias> getAliases() {
        return aliases;
    }

    /**
     * 
     * @param aliases
     *     The aliases
     */
    public void setAliases(List<Alias> aliases) {
        this.aliases = aliases;
    }

    /**
     * 
     * @return
     *     The gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * 
     * @param gender
     *     The gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

}
