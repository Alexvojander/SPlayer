package com.dmplayer.models.VkObjects.VkProfileUserDataResponse;

public class VkUserDataCollection {

    private VkUserData[] response;

    public VkUserData[] getResponse() {
        return response;
    }

    public void setResponse(VkUserData[] response) {
        this.response = response;
    }

    public String toString() {
        return "VkUserDataCollection{" +
                "response=" + response +
                '}';
    }
}