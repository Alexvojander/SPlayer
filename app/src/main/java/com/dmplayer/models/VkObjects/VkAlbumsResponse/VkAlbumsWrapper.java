package com.dmplayer.models.VkObjects.VkAlbumsResponse;

public class VkAlbumsWrapper
{
    private VkAlbumsCollection response;

    public VkAlbumsCollection getResponse ()
    {
        return response;
    }

    public void setResponse (VkAlbumsCollection response)
    {
        this.response = response;
    }

    @Override
    public String toString()
    {
        return "VkAlbumsWrapper [response = "+response+"]";
    }
}