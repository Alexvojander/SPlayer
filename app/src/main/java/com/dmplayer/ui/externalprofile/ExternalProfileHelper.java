package com.dmplayer.ui.externalprofile;

public interface ExternalProfileHelper {
    ExternalProfileModel loadProfileOnline();
    ExternalProfileModel loadProfileOffline();
    void logOut();
    boolean isLogged();
}