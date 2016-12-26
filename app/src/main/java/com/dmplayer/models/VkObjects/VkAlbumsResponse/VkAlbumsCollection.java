package com.dmplayer.models.VkObjects.VkAlbumsResponse;

import com.dmplayer.models.VkObjects.VkAlbumObject;

import java.util.Arrays;

public class VkAlbumsCollection {
    private String count;

    private VkAlbumObject[] items;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public VkAlbumObject[] getItems() {
        return items;
    }

    public void setItems(VkAlbumObject[] items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "VkAlbumsCollection{" +
                "count='" + count + '\'' +
                ", items=" + Arrays.toString(items) +
                '}';
    }
}
