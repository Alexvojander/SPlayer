package com.dmplayer.models.VkObjects.VkProfileUserDataResponse;

public class VkUserData {
    private String id;

    private String first_name;

    private String last_name;

    private String photo_100;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getFirst_name ()
    {
        return first_name;
    }

    public void setFirst_name (String first_name)
    {
        this.first_name = first_name;
    }

    public String getLast_name ()
    {
        return last_name;
    }

    public void setLast_name (String last_name)
    {
        this.last_name = last_name;
    }

    public String getPhoto_100 ()
    {
        return photo_100;
    }

    public void setPhoto_100 (String photo_100)
    {
        this.photo_100 = photo_100;
    }

    @Override
    public String toString() {
        return "VkUserData{" +
                "id='" + id + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", photo_100='" + photo_100 + '\'' +
                '}';
    }
}