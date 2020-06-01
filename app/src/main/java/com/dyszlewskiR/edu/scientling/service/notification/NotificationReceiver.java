package com.dyszlewskiR.edu.scientling.service.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(getClass().getName(), "onReceive");
        Intent notificationService = new Intent(context, NotificationService.class);
        context.startService(notificationService);
    }
}
