package com.dyszlewskiR.edu.scientling.data.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dyszlewskiR.edu.scientling.data.database.tables.LanguagesTable;
import com.dyszlewskiR.edu.scientling.data.database.creators.LanguageCreator;
import com.dyszlewskiR.edu.scientling.models.entity.Language;
import com.dyszlewskiR.edu.scientling.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import static com.dyszlewskiR.edu.scientling.data.database.tables.LanguagesTable.LanguagesColumns;
import static com.dyszlewskiR.edu.scientling.data.database.tables.LanguagesTable.TABLE_NAME;

/**
 * Created by Razjelll on 05.11.2016.
 */

public class LanguageDao extends BaseDao<Language> {


    private final String INSERT_STATEMENT =
            "INSERT INTO " + TABLE_NAME + "("
                    + LanguagesColumns.NAME + ", " + LanguagesColumns.ABBREVIATION + ", "
                    + LanguagesColumns.CODE + "," + LanguagesColumns.FLAG
                    + ") VALUES (?, ?, ?, ?)";


    /**
     * @param db obiekt klasy SQLiteDatabase, służący do nawiązywania połączenia z bazą i wykonywaniem na niej operacji.
     */
    public LanguageDao(SQLiteDatabase db) {
        super(db);
        mInsertStatement = db.compileStatement(INSERT_STATEMENT);
        mTableColumns = LanguagesTable.getColumns();
    }

    /**
     * Metoda wstawiająca nowy rekord do tabeli Languages. Wstawianie odbywa się na podstawie
     * zapytania dodanego jako stała do klasy. W tym zapytaniu wystepują znaki zapytania, które
     * są symbolami zastępczymi dla zapisywanych danych. W konstruktorze łańcuch znaków z operacją
     * INSERT jest kompilowany do obiektu klasy SQLiteStatement. Stosowanie skompilowanej instrukcji
     * zamiast nieprzetworzonego działanie kodu, ponieważ framework może wstępnie przetworzyć
     * i ponownie wykorzystać plan wykonania instrukcji. Skompilowanych instrukcji można stosować
     * tylk dla tych operacji, które zwracają maksymalnie jedną wartosć long i String, czyli jeden wiersz i
     * kolumnę. Operacja wstawiania wiersza zwraca tylko identyfikator nowego wierwsza, więc
     * instrukcje skompilowane ddoskonale nadają się do tego zadania.
     * Obiekt klasy SQLiteStatement jest polem składowym ? klasy , poniewż pozwala nam to tylko
     * raz skompilować instrukcje przy tworzeniu obiektu IDao. Na początku metody save musimy
     * wyczyścić istniejące powiązania z obiektem klasy SQLiteStatement, ponieważ obiekt może
     * przechowywać powiązania z poprzedniego użycia metody save. Następnie powiązano każdy symbol
     * zastępczy z instrukcji INSERT odpowiadającą mu wartością obiektu modelu. Po zakończeniu
     * wiązania wywoływana jest metoda executeInsert i zwracana wartośc identyfikatora określającego
     * w którym miejscu w tabeli Languages został zapisany nowo utworzony wiersz
     *
     * @param entity obiekt modelu Lanuage
     * @return identyfikator określający w którym miejscu w tabeli został zapisany nowy wiersz
     */
    @Override
    public long save(Language entity) {
        mInsertStatement.clearBindings();
        mInsertStatement.bindString(LanguagesColumns.NAME_POSITION, entity.getName());
        if (entity.getAbbreviation() != null) {
            mInsertStatement.bindString(LanguagesColumns.ABBREVIATION_POSITION, entity.getAbbreviation());
        } else {
            mInsertStatement.bindNull(LanguagesColumns.ABBREVIATION_POSITION);
        }
        mInsertStatement.bindString(LanguagesColumns.CODE_POSITION, entity.getCode());
        if (entity.getFlag() != null) {
            mInsertStatement.bindBlob(LanguagesColumns.FLAG_POSITION, BitmapUtils.toByteArray(entity.getFlag()));
        } else {
            mInsertStatement.bindNull(LanguagesColumns.FLAG_POSITION);
        }


        return mInsertStatement.executeInsert();
    }

    /**
     * Metoda służąca do aktualizacji wiersza tabeli Languages.
     * Metoda najpierw tworzy obiekt klasy ContentValues. W tym obiekcie zapisuje się pary
     * klucz-wartość z nazwami kolumn i danymi jakie chcemy zapisac w bazie. //TODO opisać ContentValue
     * Po zakończeniu przygotowań wywoływana jest metoda update obiektu SQLiteDatabase. Do metody
     * przekazujemy nazwę tabeli, wartość klauzuli WHERE i argumenty klauzuli
     *
     * @param entity obiekt modelu Language
     */
    @Override
    public void update(Language entity) {
        final ContentValues values = new ContentValues();
        values.put(LanguagesColumns.NAME, entity.getName());
        values.put(LanguagesColumns.ABBREVIATION, entity.getAbbreviation());
        values.put(LanguagesColumns.CODE, entity.getCode());
        values.put(LanguagesColumns.FLAG, BitmapUtils.toByteArray(entity.getFlag()));

        mDb.update(TABLE_NAME, values, getWhereStatement(), getWhereArguments(entity));
    }

    private String getWhereStatement() {
        return LanguagesColumns.ID + "=?";
    }

    private String[] getWhereArguments(Language entity) {
        return new String[]{String.valueOf(entity.getId())};
    }

    /**
     * Metoda służąca do usuwania określonego wiersza z tabeli Languiages.
     * Metoda najpiew, czy obiekt modelu ma zapisaną wartość id, jeżeli tak następuje usuwanie,
     * jeśli wartość identyfiaktora jest mniejsza od zera oznacza to, że obiekt nie został
     * zapisany w bazie danych. Usuwanie odbywa się za pomocą metody delete klasy SQLiteDatabase.
     * Do metody trzeba podać nazwę tabeli z której chcemy usunąć wiersz, klauzulę where oraz jej
     * parametry
     *
     * @param entity obiekt modelu Language
     */
    @Override
    public int delete(Language entity) {
        long id = entity.getId();
        if (id > 0) {
            return mDb.delete(TABLE_NAME, getWhereStatement(), getWhereArguments(entity));
        }
        return 0;
    }

    /**
     * Metoda służąca do pobierania pojedyńczego rekordu z bazy danych na podstawie podanego numeru
     * identyfikującego. Metody do pobierania danych używają do tego celu klasu Cursor. Bardzo
     * ważne w tej metodzie jest zamknięcie kursora, p[onieważ w przypadku pozostawinia kursora
     * otwartego może nastąpić wyciekanie związanych z nim zasobów.
     *
     * @param id numer idenyfikujący który wiersz ma zostać pobrany z bazy danych
     * @return
     */
    @Override
    public Language get(long id) {
        Language language = null;
        Cursor cursor = mDb.query(TABLE_NAME, mTableColumns, getWhereStatement(), getWhereArguments(id),
                null, null, null, null);
        if (cursor.moveToFirst()) {
            language = LanguageCreator.createFromCursor(cursor);
        }
        closeCursor(cursor);
        return language;
    }

    private String[] getWhereArguments(long id) {
        return new String[]{String.valueOf(id)};
    }

    @Override
    public List<Language> getAll(boolean distinct, String[] columns, String selection, String[] selectionArgs,
                                 String groupBy, String having, String orderBy, String limit) {
        List<Language> languagesList = new ArrayList<>();

        Cursor cursor = mDb.query(distinct, TABLE_NAME, columns, selection, selectionArgs,
                groupBy, having, orderBy, limit);
        if (cursor.moveToFirst()) {
            Language language;
            do {
                language = LanguageCreator.createFromCursor(cursor);
                if (language != null) {
                    languagesList.add(language);
                }
            } while (cursor.moveToNext());
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return languagesList;
    }


}
