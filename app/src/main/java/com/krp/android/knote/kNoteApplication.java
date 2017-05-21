package com.krp.android.knote;

import android.app.Application;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import com.krp.android.knote.utils.Utility;

import java.io.ByteArrayOutputStream;

/**
 * Created by purushottam on 20/5/17.
 */

public class kNoteApplication extends Application {

    public static final String TAG = kNoteApplication.class.getSimpleName();

    private SharedPreferences mPrefs;

    private WallpaperManager mWallpaperManager;
    private Drawable mCurrentSysWallpaper;

    private static kNoteApplication mInstance;
    public static kNoteApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        // start wallpaper service
        startService(new Intent(this, kNoteService.class));

        // get the wallpaper manager
        mWallpaperManager = WallpaperManager.getInstance(this);
        // get the current system wallpaper
        mCurrentSysWallpaper = mWallpaperManager.getDrawable();

        Bitmap realImage = Utility.getBitmapFromVectorDrawable(this, mCurrentSysWallpaper);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        realImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

        mPrefs = getSharedPreferences(TAG, MODE_PRIVATE);
        mPrefs.edit()
                .putString("wall", encodedImage).apply();
    }


    public WallpaperManager getWallPaperManager() {
        return mWallpaperManager;
    }

    public Drawable getCurrentSysWallpaper() {
        return mCurrentSysWallpaper;
    }

    public SharedPreferences getSharedPreferences() {
        return mPrefs;
    }

}
