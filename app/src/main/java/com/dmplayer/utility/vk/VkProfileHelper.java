package com.dmplayer.utility.vk;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.dmplayer.utility.bitmaploader.UriLoader;
import com.dmplayer.utility.bitmaploader.UrlLoader;
import com.dmplayer.dialogs.ProfileDialog;
import com.dmplayer.ui.externalprofile.ExternalProfileHelper;
import com.dmplayer.ui.externalprofile.ExternalProfileModel;
import com.dmplayer.utility.internetservices.VkApiService;
import com.dmplayer.models.VkObjects.VkAlbumsResponse.VkAlbumsWrapper;
import com.dmplayer.models.VkObjects.VkProfileModel;
import com.dmplayer.models.VkObjects.VkProfileUserDataResponse.VkUserData;
import com.dmplayer.models.VkObjects.VkProfileUserDataResponse.VkUserDataCollection;
import com.dmplayer.utility.DMPlayerUtility;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VkProfileHelper implements ExternalProfileHelper {
    private boolean logged;
    private String userId;
    private String token;


    private Context context;
    private SharedPreferences prefs;

    public static final String VK_API_URL = "https://api.vk.com/method/";
    private static final String TAG = "VkProfileHelper";

    public static final String SP_LOGGED = "VK_LOGGED";
    public static final String SP_ACCESS_TOKEN = "VK_ACCESS_TOKEN";
    public static final String SP_USER_ID = "VK_USER_ID";

    private VkProfileHelper(Context context, boolean logged , String userId, String token) {
        prefs = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);

        this.context = context;
        this.logged = logged;
        this.userId = userId;
        this.token = token;
    }

    @Override
    public void logOut() {
        logged = false;
        userId = null;
        token = null;

        eraseProfile();
        eraseAccessValues();
    }

    @Override
    public ExternalProfileModel loadProfileOnline() {
        VkProfileModel.Builder profileObjectBuilder = new VkProfileModel.Builder();
        VkApiService service = createApiService();

        loadVkUserData(service, profileObjectBuilder);
        loadVkMusicData(service, profileObjectBuilder);

        VkProfileModel model = profileObjectBuilder.build();

        saveProfile(model);
        saveAccessValues(token, userId);

        return model;
    }

    @Override
    public ExternalProfileModel loadProfileOffline() {
        return new VkProfileModel.Builder()
                .setNickname(prefs.getString("VK_USER_NICKNAME", ""))
                .setAlbumsCount(prefs.getString("VK_USER_ALBUMS_COUNT", ""))
                .setSongsCount(prefs.getString("VK_USER_SONGS_COUNT", ""))
                .setPhoto(new UriLoader().loadImage(context,
                        prefs.getString("VK_USER_PHOTO_URI", "")))
                .build();
    }

    @Override
    public boolean isLogged() {
        return logged;
    }

    private VkApiService createApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(VK_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(VkApiService.class);
    }

    private void loadVkMusicData(VkApiService service, VkProfileModel.Builder profileObjectBuilder) {
        Call<ResponseBody> callForSongsCount = service.loadSongsCount(userId, token);

        Call<VkAlbumsWrapper> callForAlbumsCount = service.loadAlbums("0", "100", userId, token);

        try {
            Response<ResponseBody> responseSongsCount = callForSongsCount.execute();
            Response<VkAlbumsWrapper> responseAlbumsCount = callForAlbumsCount.execute();

            String json = responseSongsCount.body().string();
            String songCount = json.substring(json.lastIndexOf(":") + 1, json.length() - 1);
            profileObjectBuilder.setSongsCount(songCount);

            String albumsCount = responseAlbumsCount.body().getResponse().getCount();
            profileObjectBuilder.setAlbumsCount(albumsCount);
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    private void loadVkUserData(VkApiService service, VkProfileModel.Builder profileObjectBuilder) {
        Call<VkUserDataCollection> callForUserData = service.loadUserData("photo_100", userId, token);

        try {
            Response<VkUserDataCollection> responseUserData = callForUserData.execute();

            VkUserData userData = responseUserData.body().getResponse()[0];

            profileObjectBuilder.addNicknamePart(userData.getFirst_name());
            profileObjectBuilder.addNicknamePart(" " + userData.getLast_name());
            profileObjectBuilder.setPhoto(new UrlLoader().loadImage(context,
                    userData.getPhoto_100()));
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    private void saveProfile(VkProfileModel profile) {
        DMPlayerUtility.saveBitmap(profile.getPhoto(), ProfileDialog.PHOTO_DIR_PATH, "vk_photo");

        prefs.edit()
                .putString("VK_USER_NICKNAME", profile.getNickname())
                .putString("VK_USER_SONGS_COUNT", profile.getSongsCount())
                .putString("VK_USER_ALBUMS_COUNT", profile.getAlbumsCount())
                .putString("VK_USER_PHOTO_URI", DMPlayerUtility.getUriFromPath(ProfileDialog.AVATAR_VK_FILE_PATH).toString())
                .apply();
    }

    private void saveAccessValues(String token, String userId) {
        prefs.edit()
                .putBoolean(SP_LOGGED, true)
                .putString(SP_ACCESS_TOKEN, token)
                .putString(SP_USER_ID, userId)
                .apply();
    }

    private void eraseProfile() {
        DMPlayerUtility.deleteFile(ProfileDialog.PHOTO_DIR_PATH + "vk_photo.jpg");

        prefs.edit()
                .remove("VK_USER_NICKNAME")
                .remove("VK_USER_SONGS_COUNT")
                .remove("VK_USER_ALBUMS_COUNT")
                .remove("VK_USER_PHOTO_URI")
                .apply();
    }

    private void eraseAccessValues() {
        prefs.edit()
                .remove(SP_LOGGED)
                .remove(SP_ACCESS_TOKEN)
                .remove(SP_USER_ID)
                .apply();
    }


    public static class Builder {
        private Context context;
        private boolean logged;
        private String token;
        private String userId;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setLogged(boolean logged) {
            this.logged = logged;

            return this;
        }

        public Builder setToken(String token) {
            this.token = token;

            return this;
        }

        public Builder setUserId(String userId) {
            this.userId = userId;

            return this;
        }

        public VkProfileHelper build() {
            if (!logged && token == null && userId == null) {
                SharedPreferences prefs = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);

                boolean logged = prefs.getBoolean(SP_LOGGED, false);;
                String token = prefs.getString(SP_ACCESS_TOKEN, "");
                String userId = prefs.getString(SP_USER_ID, "");

                return new VkProfileHelper(context, logged, userId, token);
            } else {
                return new VkProfileHelper(context, logged, userId, token);
            }
        }
    }
}