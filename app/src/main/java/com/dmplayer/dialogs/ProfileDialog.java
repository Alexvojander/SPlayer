package com.dmplayer.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dmplayer.R;
import com.dmplayer.ui.externalprofile.layout.ExternalProfileLayout;
import com.dmplayer.fragments.FragmentSettings;
import com.dmplayer.utility.vk.VkProfilePresenter;
import com.dmplayer.ui.CircleImageView;
import com.dmplayer.utility.DMPlayerUtility;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;

public class ProfileDialog extends BaseDialogFragment {
    public static final String PHOTO_DIR_PATH = Environment.getExternalStorageDirectory() + "/.DM_Player/DM_Player_photos";
    public static final String AVATAR_FILE_PATH = PHOTO_DIR_PATH + "/photo_avatar.jpg";
    public static final String AVATAR_TEMP_FILE_PATH = PHOTO_DIR_PATH + "/photo_avatar_temp.jpg";
    public static final String AVATAR_VK_FILE_PATH = PHOTO_DIR_PATH + "/vk_photo.jpg";

    private static final String TAG = ProfileDialog.class.getSimpleName();

    @BindView(R.id.button_ok)
    Button buttonOk;
    @BindView(R.id.button_cancel)
    Button buttonCancel;
    @BindView(R.id.user_local_name)
    EditText nickName;
    @BindView(R.id.user_local_avatar)
    CircleImageView avatar;
    @BindView(R.id.vk_profile_view)
    ExternalProfileLayout vkProfileView;

    private VkProfilePresenter vkProfilePresenter;

    private SharedPreferences sharedPreferences;
    private Uri photoFromGallery;

    private int where;
    private String initialName;
    private boolean isAvatarChanged;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_profile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                vkProfilePresenter.onAccountDataReceived(res.accessToken, res.userId);
            }

            @Override
            public void onError(VKError error) {
                Log.e(TAG, "Login response: OnError");

                Toast.makeText(getActivity(), "An error occurred while login in.",
                        Toast.LENGTH_SHORT).show();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }

        where = requestCode;

        switch (requestCode) {
            case FragmentSettings.GALLERY_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    isAvatarChanged = true;

                    photoFromGallery = data.getData();
                    DMPlayerUtility.settingPicture(avatar, photoFromGallery);
                }
                break;

            case FragmentSettings.CAMERA_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    isAvatarChanged = true;

                    Uri tempAvatarUri = DMPlayerUtility.getUriFromPath(AVATAR_TEMP_FILE_PATH);
                    DMPlayerUtility.settingPicture(avatar, tempAvatarUri);
                }
                break;
        }
    }

    private void init() {
        sharedPreferences = getActivity().getSharedPreferences("VALUES", Context.MODE_PRIVATE);

        setDefaultSettings();

        setVkProfile();
    }

    private void setDefaultSettings() {
        setAvatar();

        setName();
    }

    private void setVkProfile() {
        vkProfilePresenter = new VkProfilePresenter(vkProfileView);
        vkProfilePresenter.onCreate(this);
    }

    private void setAvatar() {
        String avatarPhoto = sharedPreferences.getString(FragmentSettings.AVATAR, "");
        Uri avatarPhotoUri = Uri.parse(avatarPhoto);

        if (DMPlayerUtility.isURIExists(avatarPhotoUri)) {
            DMPlayerUtility.settingPicture(avatar, avatarPhotoUri);
        } else {
            DMPlayerUtility.settingPicture(avatar, R.drawable.avatar_default);
        }
    }

    private void setName() {
        initialName = sharedPreferences.getString(FragmentSettings.NAME,
                getResources().getString(R.string.profile_defult_name));

        nickName.setText(initialName);
    }

    @OnClick(R.id.button_cancel)
    public void finishRefuse(Button v) {
        DMPlayerUtility.deleteFile(DMPlayerUtility.getUriFromPath(AVATAR_TEMP_FILE_PATH).getPath());

        if (getOnWorkDone() != null) {
            getOnWorkDone().onRefuse();
        }
        dismiss();
    }
    
    @OnClick(R.id.button_ok)
    public void finishAgree(Button v) {
        saveData();

        if (getOnWorkDone() != null) {
            getOnWorkDone().onAgree();
        }
        dismiss();
    }

    @OnClick(R.id.user_local_name)
    public void setEditMode(EditText v) {
        v.setCursorVisible(true);
        v.setInputType(InputType.TYPE_CLASS_TEXT);
    }

    @OnEditorAction(R.id.user_local_name)
    public boolean checkIfHideKeyboard(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_DONE) {
            DMPlayerUtility.hideKeys(getActivity(), textView);

            return true;
        }
        return false;
    }

    @OnClick(R.id.user_local_avatar)
    public void showAvatarDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle("Photo");
        String[] titles = {"Take photo from camera", "Choose from gallery"};
        OnClickListener l = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        Intent toCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        toCamera.putExtra(MediaStore.EXTRA_OUTPUT, DMPlayerUtility.getUriFromPath(AVATAR_TEMP_FILE_PATH));
                        startActivityForResult(toCamera, FragmentSettings.CAMERA_REQUEST);

                        isAvatarChanged = true;
                        break;
                    case 1:
                        Intent toGallery = new Intent(Intent.ACTION_PICK);
                        toGallery.setType("image/*");
                        startActivityForResult(toGallery, FragmentSettings.GALLERY_REQUEST);

                        isAvatarChanged = true;
                        break;
                }
            }
        };
        adb.setItems(titles, l);
        adb.show();
    }

    private void saveData() {
        String currentName = nickName.getText().toString();
        boolean isNameChanged = !initialName.equals(currentName);

        if (isNameChanged) {
            sharedPreferences.edit()
                    .putString(FragmentSettings.NAME, currentName)
                    .apply();
        }

        if (isAvatarChanged) {
            File avatarPhoto = new File(DMPlayerUtility.getUriFromPath(AVATAR_FILE_PATH).getPath());
            switch (where) {
                case FragmentSettings.GALLERY_REQUEST:
                    File pictureToCopy = new File(
                            DMPlayerUtility.getRealPathFromURI(getActivity(), photoFromGallery));
                    try {
                        DMPlayerUtility.copyFile(pictureToCopy, avatarPhoto);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }

                    DMPlayerUtility.deleteFile(DMPlayerUtility.getUriFromPath(AVATAR_TEMP_FILE_PATH).getPath());
                    break;

                case FragmentSettings.CAMERA_REQUEST:
                    File photoTaken = new File(DMPlayerUtility.getUriFromPath(AVATAR_TEMP_FILE_PATH).getPath());
                    photoTaken.renameTo(avatarPhoto);
                    break;
            }

            sharedPreferences.edit()
                    .putString(FragmentSettings.AVATAR, avatarPhoto.toURI().toString())
                    .apply();
        }
    }

    private OnWorkDone OnWorkDone;
    public OnWorkDone getOnWorkDone() {
        return OnWorkDone;
    }
    public void setOnWorkDone(OnWorkDone OnWorkDone) {
        this.OnWorkDone = OnWorkDone;
    }
}
