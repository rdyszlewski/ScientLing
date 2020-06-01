package com.dyszlewskiR.edu.scientling.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dyszlewskiR.edu.scientling.data.database.utils.QueryReader;
import com.dyszlewskiR.edu.scientling.utils.AndroidFileOpener;
import com.dyszlewskiR.edu.scientling.utils.AssetsFileOpener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa służąca do otwierania plików bazy danych. Klasa sprawdza, czy plik istnieje,
 * jeśli tak otwiera go, jeśli nie tworzy nowy
 *
 * @author Roman Dyszlewski
 * @version 0.05
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "scientling.sqlite";
    private static final int VERSION = 1;

    private static final String CREATE_FILE_NAME = "createDb";
    private static final String UPDATE_FILE_NAME = "updateDb_";
    private static final String SQL_FILE_EXTENSION = ".sql";
    private static final String SQL_FOLDER = "sql/";

    private Context mContext;
    private AndroidFileOpener mFileOpener;

    /**
     * Kontruktor klasy DatabaseHelper
     *
     * @param context kontekst aplikacji
     */
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        mContext = context;
        mFileOpener = new AssetsFileOpener(mContext);
    }

    /**
     * Metoda ustawijąca z sposób otwierania zasonów.
     *
     * @param fileOpener
     */
    public void setFileOpener(AndroidFileOpener fileOpener) {
        mFileOpener = fileOpener;
    }

    /**
     * Metoda służy do tworzenia, a także do zapełnienia bazy danych.
     * Metoda wywoywana jest w momencie, gdy odwoujemy się do bazy danych, ktora jeszcze nie istnieje.
     *
     * @param db baza sqlite
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            createDbStructure(db);
        } catch (IOException e) {
            e.printStackTrace(); //TODO stworznie własnego wyjątku
        }
    }

    /**
     * Metoda tworząca tabele w bazie danych. Tworzenie następuje na podstawie pliku zawierajacego zapytania sql tworzące tabele
     * a także wstawiające potrzebne dane. Pliki powinny znajdować się w katalogu assets projektu. Korzystanie z plików do tworzenia tabel
     * daje wiele korzyści. Jedna z nich jest to, że wszystkie polecenia jakie są nam potrzebne do stworzenia struktury bazy danych
     * są w jednym miejscu, a dodatkowo są oddzielone od kodu. W przypadku większego projektu ułatwia to rozdzielenie zadań między
     * osobą zajmującą się bazami danych a programistą aplikacji. Korzystanie z pliku do tworzenia bazy dancyh ułatwia także testowanie
     * takich zapytań. Można skorzystać ze zewnętrznych programów do zarządzania bazami SQLite i przetestować na nich jak baza będzie się
     * zachowywała po utworzeniu jej w dany sposób. Tworzenie tabel z wykorzystaniem interfejsu graficznego i generowanie kodu zapytania.
     * Taki wygenerowany kod można nastepnie wkleić do pliku i przeniść do aplikacji. Nie trzeba przekształcać go, tak jak to trzeba robić
     * jeśli chcemy przedstawić zapytanie za pomocą wartości String. W pliku sql możemy wpisać komentarze, co czyni je łatwiejsze w zrozumieniu.
     * Wadą tego rozwiązania jest możliwość niespójnego nazwenictwa tabel i kolumn w porównaniu z tymi wykorzystywanymi w aplikacji,
     * przez co istnieje możliwośc wystąpienia błędów, które są trudne do zlokalizowania.
     * Jesli wpisujemy zapytania CREATE TABLE w kodzie aplikcaji, najczęściek wykorzystywane są stałe określające nazwę tabeli
     * i jej kolumn. Stałe te wykorzystywane są takze do pobierania i zapisywania danych z bazy danych. Podczas korzystania z tego rozwiązania
     * korzystamy z tych samych nazw zarówno podczas tworzenia tabeli jak i podczas korzystania z niej, dlatego mamy pewność, że nazwy z których
     * korzystamy są poprawne.
     * Podczas wpisywani zapytań w kodzie aplikacji, możemy to zrobić za w klasie dziedziczącej po SQLiteOpenHelper, ale w przypadku
     * dużej ilości tabel metoda onCreate tej klasy może być ciężka do odczytania. Można także zdefiniować odzielne klasy dla każdej tabeli,
     * w których umieszczone będą treści zapytań, ale wiąże sie to z powstaniem sporej ilości dodatkowego kodu, którym po pewnym czasie
     * może być ciężko zarządzać. Edycja treści zapytań w kodzie aplikacje jest trudniejsza niż educja zapytania umieszczonego w pliku.
     * Najczęstczym błędem podczas wpisywania zapytania w kodzie jest brak odstępu, powstający w wyniku nieuważnej konkatenacji nazwy tabeli lub
     * kolumny z resztą zapytania.
     * <p>
     * Wszystkie wykonywania zapytań są objęte transakcją, więc nie zaistnieje sytuacja, w której zostałą stworzona tylko część bazy danych.
     * Metoda najpierw pobiera poszcególne zapytania z pliku, a następnie każde po kolei wykonuje.
     *
     * @param db baza danych, w której zostaną utworzenia tabele
     * @throws IOException
     */
    private void createDbStructure(SQLiteDatabase db) throws IOException { //TODO wyrzucenie własnego wyjątku
        //ArrayList<String> statements = getSQLStatements(SQL_FOLDER+CREATE_FILE_NAME + SQL_FILE_EXTENSION);
        List<String> statements = QueryReader.getQueries(SQL_FOLDER + CREATE_FILE_NAME + SQL_FILE_EXTENSION, mContext);
        db.beginTransaction();
        String query;
        for (int i = 0; i < statements.size(); ++i) {
            query = statements.get(i);
            db.execSQL(query);
        }
        //TODO tutaj jest wprowadzanie danych, później będzie trzeba to usunąć
        statements = getSQLStatements("testInsertStatements.sql");
        for (int i = 0; i < statements.size(); i++) {
            query = statements.get(i);
            db.execSQL(query);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private ArrayList<String> getSQLStatements(String sqlFileName) throws IOException {
        QueryReader queryReader = new QueryReader();
        InputStream queryStream = mFileOpener.getStream(sqlFileName);
        return queryReader.readFromStream(queryStream);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            updateDbStructure(db, oldVersion, newVersion);
        } catch (IOException e) {
            e.printStackTrace(); //TODO dodać własny wyjątek
        }
    }

    /**
     * Metoda służąca do wprowadzania zmian w strukturze bazy danych podczas aktualizowania bazy, czyli zwiękdzenie jego numeru wersji.
     * W tym przydapku także zdecydowano się na skorzystanie z zapytań w postaci pliku sql. Takie rozwiązanie jest bardzo wygodne podczas
     * aktualizacji bazy danych. Do zmienienia struktury bazy danych wystarczy tylko dodanie pliku z poleceniami tych zmian. Pliki mają
     * specjaną nazwę, która pozwala rozpoznać metodzie, które pliki mają być użyte aby zaktualizowac bazę danych do najnowszej wersji.
     * Przyjęto następującą nazwę dla plików zawierające polecenia sql do aktualizowani bazy "updateDB_{wersja}.sql".
     * Pliki tak samo jak w przypadku tworzenia tabel ułatwiają testowanie i zmniejszają ilość potrzebnego kodu. Jako alternatywa jest wpisywania
     * zapytań w kodzie. Wiąże się to z tym, ze po każdej aktualizacji, konieczneby było tworznie nowej metody, która aktualizowałaby
     * z jdenej wersji do drugiej. Takie rozwiązanie znacznie zwiększa ilośc kodu, ponieważ wymaga wpisywania treści poszczególnych
     * metod, a także warunków if, które określałyby które metody powinny być wykonane. Jako alternatywę dla ifów można wykorzystać
     * switch bez poleceń break, dzięki czemu jeśli już pierwszą metodę którą powinna zostać wykonana, wykonają sie wszystkie następne.
     * <p>
     * Metoda odczytuje wszystkie pliki, gdzie numer wesji jest większy niż dotychczasowa wersja bazy.
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     * @throws IOException
     */
    private void updateDbStructure(SQLiteDatabase db, int oldVersion, int newVersion) throws IOException {
        db.beginTransaction();
        for (int version = (oldVersion + 1); version < (newVersion + 1); ++version)//TODO przetestować czy jest dobry warunek zakończenia
        {
            ArrayList<String> statements = getSQLStatements(UPDATE_FILE_NAME + version + SQL_FILE_EXTENSION);
            for (int i = 0; i < statements.size(); ++i) {
                String query = statements.get(i);
                db.execSQL(query);
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            Log.d("DatabaseHelper", "foreign_keys = ON");
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }


}
