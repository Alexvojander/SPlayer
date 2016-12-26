/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.dmplayer;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.dmplayer.db.DMPLayerDBHelper;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.vk.sdk.VKSdk;

public class DMPlayerApplication extends Application {

    public static Context applicationContext;
    public static volatile Handler applicationHandler;
    public static Point displaySize = new Point();
    public static float density = 1;

    private static final String TAG = DMPlayerApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        applicationContext = getApplicationContext();
        applicationHandler = new Handler(applicationContext.getMainLooper());

        /**
         * Data base initialize
         */
        initializeDB();
        /*
         * Display Density Calculation so that Application not problem with ALL
		 * resolution.
		 */
        checkDisplaySize();
        density = applicationContext.getResources().getDisplayMetrics().density;

		/*
         * Imageloader initialize
		 */
        initImageLoader(applicationContext);

        VKSdk.initialize(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Initialize Image Loader.
     */
    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }


    public static int dp(float value) {
        return (int) Math.ceil(density * value);
    }

    public static void checkDisplaySize() {
        try {
            WindowManager manager = (WindowManager) applicationContext.getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                Display display = manager.getDefaultDisplay();
                if (display != null) {
                        display.getSize(displaySize);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Related to Data Base.
     */
    public DMPLayerDBHelper DB_HELPER;

    private void initializeDB() {
        if (DB_HELPER == null) {
            DB_HELPER = new DMPLayerDBHelper(DMPlayerApplication.this);
        }
        try {
            DB_HELPER.getWritableDatabase();
            DB_HELPER.openDataBase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeDB() {
        try {
            DB_HELPER.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
