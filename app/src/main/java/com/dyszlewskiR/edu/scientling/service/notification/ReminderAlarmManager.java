package com.dyszlewskiR.edu.scientling.service.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dyszlewskiR.edu.scientling.service.preferences.Preferences;

import java.util.Calendar;

public class ReminderAlarmManager {

    private static final String TAG = "ReminderAlarmManager";

    public static void startAlarm(Context context) {
        Log.d(TAG, "startAlarm");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, getDate(context).getTimeInMillis(), AlarmManager.INTERVAL_DAY, getSender(context));
    }

    public static void stopAlarm(Context context) {
        Log.d(TAG, "stopAlarm");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getSender(context));
    }

    private static PendingIntent getSender(Context context) {
        Intent receiver = new Intent(context, NotificationReceiver.class);
        return PendingIntent.getBroadcast(context, 0, receiver, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    /**
     * Metoda która pobiera godzinę przypomnienia z preferencji, a następnie buduje kalendarz
     * i ustawia odpowienią godzinę.
     *
     * @param context kontekst aplikacji potrzebny do pobrania godziny z preferencji
     * @return kalendarz który posłuży do ustawienia AlarmManager
     */
    private static Calendar getDate(Context context) {
        String time = Preferences.getReminderTime(context);
        Calendar calendar = Calendar.getInstance();
        Log.d(TAG, "getDate" + calendar.toString());
        calendar.set(Calendar.HOUR_OF_DAY, getHour(time));
        calendar.set(Calendar.MINUTE, getMinute(time));

        Log.d(TAG, "getDate " + calendar.toString());
        return calendar;
    }

    private static int getHour(String time) {
        return Integer.parseInt(time.split(":")[0]);
    }

    private static int getMinute(String time) {
        return Integer.parseInt(time.split(":")[1]);
    }
}
