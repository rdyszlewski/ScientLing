package com.dyszlewskiR.edu.scientling.service.management;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.data.file.MediaFileSystem;
import com.dyszlewskiR.edu.scientling.models.entity.Lesson;
import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.service.net.values.MediaType;

import java.util.List;

/**
 * Serwis który usuwa lekcje z bazy danych. Słówka które są częścia usuwanej lekcji mogą być usunięte
 * lub przeniesione do innej leskcji. Do usuwania lekcji został stworzony serwis, ponieważ
 * jest to operacja długotrwała i nie powinna być przerywana kiedy użytkownik opuści aktywność.
 * Klasa usuwa zarówno dane z bazy danych, a takze wszystkie obrazy i nagrania powiązane z tymi
 * usuniętymi słówkami.
 */

public class DeletingLessonService extends Service {

    private final String LOG_TAG = "DeletingService";
    private DataManager mDataManager;

    @Override
    public void onCreate() {
        mDataManager = ((LingApplication) getApplication()).getDataManager();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        long id = intent.getLongExtra("lesson", -1);
        Lesson lesson = new Lesson(id);
        long newLessonId = intent.getLongExtra("newLessonId", -1);
        long setId = intent.getLongExtra("set", -1);
        VocabularySet set = mDataManager.getSetById(setId);
        DeletingRunnable runnable = new DeletingRunnable(lesson, newLessonId, set.getCatalog(), mDataManager);
        runnable.run();
        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class DeletingRunnable implements Runnable {

        private Lesson mLesson;
        private long mNewLessonId;
        private String mCatalog;
        private DataManager mDataManager;

        public DeletingRunnable(Lesson lesson, long newLessonId, String catalog, DataManager dataManager) {
            mLesson = lesson;
            mNewLessonId = newLessonId;
            mCatalog = catalog;
            mDataManager = dataManager;
        }


        @Override
        public void run() {
            deleteLesson();
        }

        /**
         * Metoda usuwająca lekcję z bazy danych oraz wszystkie pliki powiązane ze słówkami które należały
         * do tej lekcji
         * Najpierw pobieramy listę obrazków powiązanych z usuwanymi słówkami. Robimy to na początku
         * w razie niepowodzenia usuwania z bazy danych. Jeśli wystąpi błąd podczas usuwania lekcji
         * z bazy nie zostaną usunięte obrazki
         * Później pobieramy listę nagrań
         * Usuwamy lekcje z bazy danych. Podczas usuwania lekcji wszystkie słówka które do niej należły
         * także zostaną usunięte lub przeniesione do innej lekcji
         * Jeżeli słówka zostały usunięte z bazy usuwamy nagrania i obrazki.
         */
        private void deleteLesson() {
            List<String> imagesList = null;
            List<String> recordsList = null;
            if (mNewLessonId == -1) { //robimy to w przypadku jeśli usuwane są słówka z bazy danych
                imagesList = mDataManager.getImagesNamesFromLesson(mLesson.getId());
                recordsList = mDataManager.getRecordsNamesFromLesson(mLesson.getId());
            }
            mDataManager.deleteLesson(mLesson, mNewLessonId);
            if (mNewLessonId == -1) {
                deleteImages(imagesList);
                deleteRecords(recordsList);
            }
        }

        private void deleteImages(List<String> imagesList) {
            for (String imageName : imagesList) {
                MediaFileSystem.deleteMedia(imageName, mCatalog, MediaType.IMAGES, getBaseContext());
            }
        }

        private void deleteRecords(List<String> recordsList) {
            for (String recordName : recordsList) {
                MediaFileSystem.deleteMedia(recordName, mCatalog,MediaType.RECORDS, getBaseContext());
            }
        }
    }
}
