package com.dmplayer.utility.vk;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;

import com.dmplayer.ui.externalprofile.ExternalProfilePresenter;
import com.dmplayer.ui.externalprofile.ExternalProfileView;
import com.dmplayer.ui.externalprofile.ExternalProfileViewCallbacks;
import com.dmplayer.models.VkObjects.VkProfileModel;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

public class VkProfilePresenter implements ExternalProfilePresenter {
    private final ExternalProfileView viewActions;

    private VkProfileHelper profileHelper;

    private Context context;

    public VkProfilePresenter(ExternalProfileView viewActions) {
        this.viewActions = viewActions;
    }

    @Override
    public void onCreate(final Fragment fragment) {
        context = fragment.getActivity();

        profileHelper = new VkProfileHelper.Builder(context)
                .build();

        if (profileHelper.isLogged()) {
            VkProfileModel profile = (VkProfileModel) profileHelper.loadProfileOffline();
            viewActions.showProfile(profile);
        } else {
            viewActions.showLogIn();
        }

        viewActions.setCallbacks(new ExternalProfileViewCallbacks() {
            @Override
            public void onLogIn() {
                VKSdk.login(fragment, VKScope.AUDIO, VKScope.OFFLINE);
            }

            @Override
            public void onRefresh() {
                new LoadProfileTask().execute();
            }

            @Override
            public void onLogOut() {
                profileHelper.logOut();
                viewActions.showLogIn();
            }
        });
    }

    @Override
    public void onAccountDataReceived(String token, String userId) {
        profileHelper = new VkProfileHelper.Builder(context)
                .setLogged(true)
                .setToken(token)
                .setUserId(userId)
                .build();

        new LoadProfileTask().execute();
    }

    @Override
    public void onDestroy() {
        profileHelper.logOut();
        viewActions.showLogIn();
    }

    private class LoadProfileTask extends AsyncTask<Void, Void, VkProfileModel> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            viewActions.showLoading();
        }

        @Override
        protected VkProfileModel doInBackground(Void... voids) {
            return (VkProfileModel) profileHelper.loadProfileOnline();
        }

        @Override
        protected void onPostExecute(VkProfileModel profile) {
            super.onPostExecute(profile);

            viewActions.showProfile(profile);
        }
    }
}
