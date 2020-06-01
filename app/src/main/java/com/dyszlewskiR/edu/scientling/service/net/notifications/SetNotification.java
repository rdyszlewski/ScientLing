package com.dyszlewskiR.edu.scientling.service.net.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.dyszlewskiR.edu.scientling.R;

import java.util.Random;

public class SetNotification {
    private int mNotificationId;
    private Notification.Builder mNotificationBuilder;
    private NotificationManager mNotificationManager;

    public void create(Context context){
        create("","",context);
    }

    public void create(String title, String content, Context context){
        mNotificationId = randomNotificationNumber();
        mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = new Notification.Builder(context.getApplicationContext());
        mNotificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_hint)
                .setContentTitle(title)
                .setContentText(content)
                .setProgress(100, 0, false);
        mNotificationManager.notify(mNotificationId, build());
    }

    private int randomNotificationNumber() {
        Random random = new Random();
        return random.nextInt(9999);
    }

    public Notification build(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return mNotificationBuilder.getNotification();
        } else {
            return mNotificationBuilder.build();
        }
    }

    public void setProgress(int progress){
        mNotificationBuilder.setProgress(100, progress, false);
    }

    public void setTitle(String title){
        mNotificationBuilder.setContentTitle(title);
    }

    public void setContent(String content){
        mNotificationBuilder.setContentText(content);
    }

    public void send(){
        mNotificationManager.notify(mNotificationId, build());
    }

    public void hideProgress(){
        mNotificationBuilder.setProgress(0, 0, false);
    }

    public int getId(){
        return mNotificationId;
    }

}
