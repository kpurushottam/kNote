package com.krp.android.knote;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.krp.android.knote.utils.Utility;

import java.io.IOException;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class HomeActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    public static final String TAG = HomeActivity.class.getSimpleName();
    private static final int RC_CAMERA_PERM = 123;

    private ImageView mImgWallpaper;
    private WallpaperManager mWallpaperManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_lock_screen);

        mWallpaperManager = kNoteApplication.getInstance().getWallPaperManager();

        mImgWallpaper = (ImageView) findViewById(R.id.wallpaper);

        Bitmap currentWallpaper = Utility.getBitmapFromVectorDrawable(getApplicationContext(),
                kNoteApplication.getInstance().getCurrentSysWallpaper());

        //Bitmap bitmap = Utility.getMarkerBitmapFromView(this, 0);
        mImgWallpaper.setImageBitmap(currentWallpaper);

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE : {
                    // Do something after user returned from app settings screen, like showing a Toast.
                    Toast.makeText(this, "Returned from app settings to activity", Toast.LENGTH_SHORT)
                            .show();
                }
                /*
                case REQUEST_CODE_TAKE_PICTURE *//*2*//*:
                    tmpbitmap = getBitmap(this.mFileTemp.getPath());
                    if (tmpbitmap == null) {
                        Toast.makeText(this, "Somthing wrong", Toast.LENGTH_LONG).show();
                        break;
                    } else {
                        mCapturedPhotos.add(tmpbitmap);
                        showDial();
                        break;
                    }
                case REQUEST_CODE_CROP_IMAGE *//*3*//*:
                    BitmapFactory.decodeFile(this.mFileTemp.getPath());
                    break*/;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        wallpaperTask();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @AfterPermissionGranted(RC_CAMERA_PERM)
    public void wallpaperTask() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.SET_WALLPAPER)) {
            // Have permission, do the thing!
            //setWallpaper();

        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, "Please provide camera permission to set wallpaper",
                    RC_CAMERA_PERM, Manifest.permission.SET_WALLPAPER);
        }
    }

    private void setWallpaper() {
        try {
            Bitmap currentWallpaper = Utility.getBitmapFromVectorDrawable(getApplicationContext(),
                    kNoteApplication.getInstance().getCurrentSysWallpaper());

            Bitmap bitmap = Utility.getMarkerBitmapFromView(HomeActivity.this);
            mImgWallpaper.setImageBitmap(currentWallpaper);
            WallpaperManager.getInstance(this).setBitmap(bitmap/*,
                    null, true, WallpaperManager.FLAG_LOCK*/);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        // Some permissions have been granted
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

}
