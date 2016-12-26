package com.dmplayer.utility.bitmaploader;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;

import java.util.concurrent.ExecutionException;

public class UrlLoader implements BitmapLoader {
    private final static String TAG = "UrlLoader";

    @Override
    public Bitmap loadImage(Context context, String resource) throws IllegalArgumentException {
        try {
            return Glide.with(context)
            .load(resource)
            .asBitmap()
            .into(100, 100)
            .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalArgumentException(TAG + " exception " + e.getMessage());
        }
    }
}
