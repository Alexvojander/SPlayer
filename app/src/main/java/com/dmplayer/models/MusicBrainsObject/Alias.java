
package com.dmplayer.models.MusicBrainsObject;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Alias {

    @SerializedName("sort-name")
    @Expose
    private String sortName;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("locale")
    @Expose
    private Object locale;
    @SerializedName("type")
    @Expose
    private Object type;
    @SerializedName("primary")
    @Expose
    private Object primary;
    @SerializedName("begin-date")
    @Expose
    private Object beginDate;
    @SerializedName("end-date")
    @Expose
    private Object endDate;

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
     *     The locale
     */
    public Object getLocale() {
        return locale;
    }

    /**
     * 
     * @param locale
     *     The locale
     */
    public void setLocale(Object locale) {
        this.locale = locale;
    }

    /**
     * 
     * @return
     *     The type
     */
    public Object getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    public void setType(Object type) {
        this.type = type;
    }

    /**
     * 
     * @return
     *     The primary
     */
    public Object getPrimary() {
        return primary;
    }

    /**
     * 
     * @param primary
     *     The primary
     */
    public void setPrimary(Object primary) {
        this.primary = primary;
    }

    /**
     * 
     * @return
     *     The beginDate
     */
    public Object getBeginDate() {
        return beginDate;
    }

    /**
     * 
     * @param beginDate
     *     The begin-date
     */
    public void setBeginDate(Object beginDate) {
        this.beginDate = beginDate;
    }

    /**
     * 
     * @return
     *     The endDate
     */
    public Object getEndDate() {
        return endDate;
    }

    /**
     * 
     * @param endDate
     *     The end-date
     */
    public void setEndDate(Object endDate) {
        this.endDate = endDate;
    }

}
