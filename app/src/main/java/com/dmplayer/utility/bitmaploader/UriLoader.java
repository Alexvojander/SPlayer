package com.dmplayer.utility.bitmaploader;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;

public class UriLoader implements BitmapLoader {
    private final static String TAG = "UriLoader";

    @Override
    public Bitmap loadImage(Context context, String resource) throws IllegalArgumentException {
        try {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(resource));
        } catch (IOException e) {
            throw new IllegalArgumentException(TAG + " exception " + e.getMessage());
        }
    }
}
