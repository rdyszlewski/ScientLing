package com.dyszlewskiR.edu.scientling.service.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.presentation.activity.MainActivity;
import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.service.preferences.Preferences;
import com.dyszlewskiR.edu.scientling.utils.DateCalculator;
import com.dyszlewskiR.edu.scientling.utils.DateUtils;

import java.util.List;

public class NotificationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(getClass().getName(), "onStartCommand");
        checkRepetition();
        return Service.START_STICKY;
    }

    private void checkRepetition() {
        DataManager dataManager = ((LingApplication) getApplication()).getDataManager();
        List<VocabularySet> sets = dataManager.getSets();
        StringBuilder stringBuilder = null;
        int repetitionCount;
        Log.d(getClass().getName(), "sets : " + sets.size());
        for (VocabularySet set : sets) {
            repetitionCount = getRepetitionToday(set.getId(), dataManager);
            if (repetitionCount > 0) {
                if (stringBuilder == null) {
                    stringBuilder = new StringBuilder();
                }
                stringBuilder.append(set.getName()).append(" : ").append(repetitionCount).append("\n");
            }
        }

        if (stringBuilder != null) {
            Log.d(getClass().getName(), stringBuilder.toString());
            createNotification(stringBuilder.toString());
        }
    }

    private int getRepetitionToday(long setId, DataManager dataManager) {
        int date = DateCalculator.dateToInt(DateUtils.getTodayDate());
        return dataManager.getRepetitionsCount(setId, date);
    }

    private void createNotification(String content) {
        Log.d(getClass().getName(), "createNotification");
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_hint); //TODO ikona tymczasowa
        mBuilder.setContentTitle(getString(R.string.repetitions));
        mBuilder.setContentText(content);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);

        mBuilder.setContentIntent(pendingIntent);
        //sprawdzamy czy w preferencji jest ustawiony dźwięk przy powiadomieniu
        if (Preferences.getReminderSound(getBaseContext())) {
            //ustawiamy domyślny dźwięk
            mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        }
        //sprawdzamy czy w preferencji jest ustawiona wibracja przy powiadomieniu
        if (Preferences.getReminderVibration(getBaseContext())) {
            //ustawiamy domyślną wibrację
            //TODO utworzyć wibrację ze wzorca
            mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        }
        //mBuilder.setAutoCancel(true);
        //TODO można dodać jakieś akcje

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = mBuilder.build();
        notificationManager.notify(456, notification);
        Log.d(getClass().getName(), "notificationManager.notify");
    }
}
