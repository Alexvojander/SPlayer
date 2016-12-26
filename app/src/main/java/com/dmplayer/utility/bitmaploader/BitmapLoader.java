package com.dmplayer.utility.bitmaploader;

import android.content.Context;
import android.graphics.Bitmap;

public interface BitmapLoader {
    Bitmap loadImage(Context context, String resource) throws IllegalArgumentException;
}
