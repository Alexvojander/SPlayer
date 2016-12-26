package com.dmplayer.models.VkObjects.VkAudioGetResponce;

import com.dmplayer.models.VkObjects.VkAudioObject;

public class VkAudioCollection {
    private String count;

    private VkAudioObject[] items;

    public String getCount ()
    {
        return count;
    }

    public void setCount (String count)
    {
        this.count = count;
    }

    public VkAudioObject[] getItems ()
    {
        return items;
    }

    public void setItems (VkAudioObject[] items)
    {
        this.items = items;
    }

    @Override
    public String toString() {
        return "VkAudioCollection [count = " + count + ", items = " + items + "]";
    }
}
