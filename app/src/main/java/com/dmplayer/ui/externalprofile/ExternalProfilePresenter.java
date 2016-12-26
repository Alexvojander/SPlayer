package com.dmplayer.ui.externalprofile;

import android.app.Fragment;

public interface ExternalProfilePresenter {
    void onCreate(Fragment fragment);
    void onAccountDataReceived(String token, String userId);
    void onDestroy();
}
