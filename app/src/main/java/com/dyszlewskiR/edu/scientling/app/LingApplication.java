package com.dyszlewskiR.edu.scientling.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.dyszlewskiR.edu.scientling.service.preferences.PreferenceInitializer;
import com.dyszlewskiR.edu.scientling.service.preferences.Settings;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.utils.Constants;

public class LingApplication extends Application {

    private final String TAG = "LingApplication";

    private static LingApplication mInstance;

    private DataManager mDataManager;
    private long mCurrentSetId;
    private long mCurrentLessonId;

    public static synchronized LingApplication getInstance(){
        return mInstance;
    }

    public DataManager getDataManager() {
        return mDataManager;
    }

    public long getCurrentSetId() {
        return mCurrentSetId;
    }

    public long getCurrentLessonId() {
        return mCurrentLessonId;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        mInstance = this;
        mDataManager = new DataManager(getBaseContext());
        mCurrentSetId = Settings.getCurrentSetId(getBaseContext());
        if (!PreferenceInitializer.isInitialize(getBaseContext())) {
            PreferenceInitializer.initialize(getBaseContext());
            Settings.setCurrentSetId(Constants.DEFAULT_SET_ID, getBaseContext());
        }

        ApplicationLifecycleHandler handler = new ApplicationLifecycleHandler();
        registerActivityLifecycleCallbacks(handler);
        registerComponentCallbacks(handler);
    }


    @Override
    public void onLowMemory() {
        Log.d(TAG, "onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        Log.d(TAG, "onTerminate");
        mDataManager.release();
        super.onTerminate();
    }

    public boolean isServiceRunning(Class<?> serviceClass){
        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }
}
