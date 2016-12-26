package com.dmplayer.ui.externalprofile;

public interface ExternalProfileView {
    void setCallbacks(ExternalProfileViewCallbacks callbacks);
    void showProfile(ExternalProfileModel profile);
    void showLogIn();
    void showLoading();
}