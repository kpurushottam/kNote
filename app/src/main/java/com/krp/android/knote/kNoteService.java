package com.krp.android.knote;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;

import com.krp.android.knote.utils.Utility;

import java.io.File;
import java.io.IOException;

public class kNoteService extends Service {

    public static final String TAG = kNoteService.class.getSimpleName();
    public static final String ACTION_ADD_NOTE = "com.krp.android.knote.add";

    public BroadcastReceiver mScreenOnOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null) {
                switch (intent.getAction()) {
                    case Intent.ACTION_SCREEN_OFF : {
                        Bitmap bitmap = Utility.getMarkerBitmapFromView(kNoteService.this);
                        try {
                            WallpaperManager.getInstance(kNoteService.this).setBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }

                    case Intent.ACTION_SCREEN_ON : {
                        handleTodo();
                        break;
                    }
                }
            }
        }
    };


    public BroadcastReceiver mAddNoteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null) {
                switch (intent.getAction()) {
                    case ACTION_ADD_NOTE : {
                        // TODO here...
                        break;
                    }
                }
            }
        }
    };

    public kNoteService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // create kNote notification
        createDefaultNotification("none", "none");
        // create screen-lock-unlock intent-filter
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        // register screen-lock-unlock receiver
        registerReceiver(mScreenOnOffReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void handleTodo() {
        Bitmap currentWallpaper = Utility.getBitmapFromVectorDrawable(getApplicationContext(),
                kNoteApplication.getInstance().getCurrentSysWallpaper());

        String previouslyEncodedImage = kNoteApplication.getInstance().getSharedPreferences()
                .getString("wall", "");

        if( !previouslyEncodedImage.equalsIgnoreCase("") ){
            byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
            currentWallpaper = BitmapFactory.decodeByteArray(b, 0, b.length);
        }

        try {
            kNoteApplication.getInstance().getWallPaperManager()
                    .setBitmap(currentWallpaper);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


int id=-1;
    private void createDefaultNotification(String path, String fileName) {
        // Set Notification Title
        String strtitle = "Manage your ToDo's...";
        // Set Notification Text
        String strtext = "We never let you miss your priorites";
        // Set Notification id
        id++;

        // Open NotificationView Class on FileObserverService Click
        Intent noteIntent = new Intent(ACTION_ADD_NOTE);
        // Open FileObserverService.java Service
        PendingIntent pNoteIntent = PendingIntent.getBroadcast(this, 0, noteIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        // Open application, on click of "View All" on notification
        Intent homeIntent = new Intent(this, HomeActivity.class);
        // Open HomeActivity.class on clik
        PendingIntent pHomeIntent = PendingIntent.getActivity(this, 0, homeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_event_note_black_24dp);
        Bitmap screenshot = BitmapFactory.decodeFile(new File(path+fileName).getAbsolutePath(),
                new BitmapFactory.Options());

        //Create Notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                // Set Icon
                .setSmallIcon(R.drawable.ic_event_note_black_24dp)
                .setLargeIcon(bitmap)
                // Set Ticker Message
                .setTicker("Note created")
                // Set Title
                .setContentTitle(strtitle)
                // Set Text
                .setContentText(strtext)
                .setNumber(5)
//                .setStyle(new NotificationCompat.BigPictureStyle()
//                        .bigPicture(screenshot))
                // Add an Action Button below Notification
                .addAction(R.drawable.ic_note_add_black_24dp, "Add Note", pNoteIntent)
                .addAction(R.drawable.ic_library_books_black_24dp, "View All", pHomeIntent)
                // Set PendingIntent into Notification
//                .setContentIntent(pIntent)
                // Dismiss Notification
                .setOngoing(true);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(id, builder.build());
    }
}
