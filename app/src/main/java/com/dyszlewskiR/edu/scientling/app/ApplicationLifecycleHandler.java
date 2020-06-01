package com.dyszlewskiR.edu.scientling.app;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import com.dyszlewskiR.edu.scientling.service.notification.ReminderAlarmManager;

class ApplicationLifecycleHandler implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {

    private static final String TAG = ApplicationLifecycleHandler.class.getSimpleName();
    private static boolean mIsInBackground = true; //ustawiamy na true, aby onActivityResumed zostało wykonane przy pierwszym uruchomieniu
    private Context mContext;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated" + activity.getComponentName());
        if (mContext == null) {
            mContext = activity.getApplicationContext();
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (mIsInBackground) {
            Log.d(TAG, "stopAlarm");
            ReminderAlarmManager.stopAlarm(activity.getBaseContext());
            mIsInBackground = false;
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onTrimMemory(int level) {
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            Log.d(TAG, "onTrimMemory");
            if (mContext != null) {
                Log.d(TAG, "startAlarm");
                ReminderAlarmManager.startAlarm(mContext);
            }
            mIsInBackground = true;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    public void onLowMemory() {

    }
}
