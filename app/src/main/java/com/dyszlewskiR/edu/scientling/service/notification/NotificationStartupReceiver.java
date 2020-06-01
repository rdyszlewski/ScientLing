package com.dyszlewskiR.edu.scientling.service.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dyszlewskiR.edu.scientling.service.preferences.Preferences;


/**
 * Broadcast Receiver który jest uruchamiany podczas rozruchu urządzenia.
 * Jego jedynym zadaniem jest uruchomienia alarmu systemowego na określoną godzinę. Ten alarm
 * uruchamia inny broadcast receiver.
 * AlarmManager działą
 * w interwałąch równych jeden dzień. Dzięki temu nie trzeba korzystać z serwisów i cyklicznie
 * sprawdzać czasu, co może pochłonąć sporą ilość baterii.
 * Do alarmmanagera podpięta jest intencja, która zostaje uruchomiona
 */

public class NotificationStartupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //na początku sprawdzamy czy przypomnienia sąwłączone. Jeżeli nie są nie robimy nic, ponieważ nie ma sensu sprawdzania
        // czy istnieją jakieś powtórki, jeśli i tak o tym nie poinformujemy użytkownika
        if (Preferences.getReminderEnabled(context)) {
            Log.d(getClass().getName(), "onReceive");
            ReminderAlarmManager.startAlarm(context);
        }
    }
}
