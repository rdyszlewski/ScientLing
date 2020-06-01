package com.dyszlewskiR.edu.scientling.data.database.utils;

import android.content.Context;

import com.dyszlewskiR.edu.scientling.utils.AssetsFileOpener;
import com.dyszlewskiR.edu.scientling.utils.ResourcesFileOpener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Klasa służąca do odczytywania plików sql zawierajacych zapytania do twotzenia i aktualizacji bazy danych.
 */

public class QueryReader {

    //private InputStream mStream;

    private static final String STATEMENTS_SEP = ";";
    private static final String OPEN_BRACKET = "(";
    private static final String COMMENT = "--";
    private static final String START_COMMENT = "/*";
    private static final String END_COMMENT = "*/";

    /**
     * Metoda służąca do odczytywania plików sql w postani strumienia wejściowego. Skorzystano ze strumienia wejściowego
     * ponieważ w Androidzie dostep do plików odbywa się albo przez system plików, gdzie trzeba znać położenie danego pliku.
     * Sprawę mogą komplikować dwa rodzaje pamięci - wewnętrzna i zewnętrzna. Drugim sposobem jest korzystanie z pliku jako zasobu.
     * Aby otworzyć taki plik, potrzebma jest klasa Context, która jest składową Androida. Klasa QueryReader jest klasą narzędziową,
     * więc nie chcialem, aby miała jakiekolwiek powiązania z API Androida. Dzięki temu, klasa w łatwiejszy sposób moze być wykorzystywana
     * w innych projektach.
     * Metoda korzysta z kontenera ArrayList<String> w którym są przechowywane pojszczególne zapytania.
     * Podczas parsowania przechodzimy po każdym słowie odzielnie. Pozwala to na pozbycie się zbędnych spacji, dzięki czemu
     * wynikowe zapytanie będzie szczuplejsze. Strumien wejściowy może zawierać kilka zapytań. Każde z takich zapytań musi być zakończone znakiem ;,
     * aby metoda rozpoznała jego koniec. Jeśli Scanner odczyta słowo zawierający znak ;, dodaje słowo do listy zapytań, czyści obiekt klasy StringBuilder
     * a następnie przeszukuje pozostalą część strumienia. Jeśli w danym słowie nie ma znaku kończącego zapytanie dodaje do wyniku spację, ponieważ pozwala to odzielić
     * od siebie poszczególne słowa. Jeśli Scanner przejdzie do końca struminia algorytm kończy się a metoda zwaraca listę zapytań.
     *
     * @param stream
     * @return
     * @throws IOException
     */
    public ArrayList<String> readFromStream(InputStream stream) {  //TODO refaktoryzacja
        ArrayList<String> statementsList = new ArrayList<>();
        if (stream == null) {
            return statementsList;
        }
        StringBuilder statementBuilder = new StringBuilder();
        Scanner scanner = new Scanner(stream);
        String word;
        while (scanner.hasNext()) //TODO refaktoryzacja
        {
            word = scanner.next();
            if (word.contains(COMMENT)) {
                int commentBeginIndex = word.indexOf(COMMENT);
                word = word.substring(0, commentBeginIndex);
                scanner.nextLine();
            } else {
                if (word.contains(START_COMMENT)) {
                    int commentBeginIndex = word.indexOf(START_COMMENT);
                    if (word.contains(END_COMMENT)) {
                        int commentEndIndex = word.indexOf(END_COMMENT);
                        word = word.substring(0, commentBeginIndex) + " " +
                                word.substring(commentEndIndex + 2, word.length());
                    } else {
                        word = word.substring(0, commentBeginIndex);
                    }
                }
                if (word.contains(END_COMMENT)) {
                    int commentEndIndex = word.indexOf(END_COMMENT);
                    word = word.substring(commentEndIndex + 2, word.length());
                }
            }
            if (!word.equals("")) {
                statementBuilder.append(word);
                if (word.contains(STATEMENTS_SEP)) {

                    statementsList.add(statementBuilder.toString());
                    statementBuilder.setLength(0);
                } else {
                    statementBuilder.append(" ");
                }
            }
        }
        return statementsList;
    }

    public static String getQuery(String assetPath, Context context) throws IOException {
        AssetsFileOpener opener = new AssetsFileOpener(context);
        InputStream inputStream = opener.getStream(assetPath);
        Scanner scanner = new Scanner(inputStream);
        return getQueryFromScanner(scanner);
    }

    public static String getQuery(String javaResourcePath) throws IOException {
        ResourcesFileOpener opener = new ResourcesFileOpener();
        InputStream inputStream = opener.getStream(javaResourcePath);
        Scanner scanner = new Scanner(inputStream);
        return getQueryFromScanner(scanner);
    }

    public static List<String> getQueries(String queryPath, Context context) throws IOException {
        AssetsFileOpener opener = new AssetsFileOpener(context);
        InputStream inputStream = opener.getStream(queryPath);
        Scanner scanner = new Scanner(inputStream);
        List<String> queriesList = new ArrayList<>();
        String query;
        while (scanner.hasNext()) {
            query = getQueryFromScanner(scanner);
            if (query != null) {
                queriesList.add(query);
            }
        }
        return queriesList;
    }

    public static List<String> getQueries(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);
        List<String> queriesList = new ArrayList<>();
        String query;
        while (scanner.hasNext()) {
            query = getQueryFromScanner(scanner);
            if (query != null) {
                queriesList.add(query);
            }
        }
        return queriesList;
    }

    private static String getQueryFromScanner(Scanner scanner) {
        StringBuilder queryBuilder = new StringBuilder();
        String word = null;
        if (!scanner.hasNext()) {
            return null;
        }
        do { //wykonujemy do końca pliku lub do napotkania znaku końca zapytania
            word = scanner.next();
            if (word.contains(COMMENT)) {
                word = parseComment(word);
                scanner.nextLine();
            } else if (word.contains(START_COMMENT)) {
                word = parseStartComment(word);
            } else if (word.contains(END_COMMENT)) {
                word = parseStartComment(word);
            }
            if (!word.equals("")) {
                queryBuilder.append(word).append(" ");
            }
        } while (scanner.hasNext() && !word.contains(STATEMENTS_SEP));
        return queryBuilder.toString();
    }

    private static String parseComment(String word) {
        int commentBeginIndex = word.indexOf(COMMENT);
        return  word.substring(0, commentBeginIndex);
    }

    private static String parseStartComment(String word) {
        int commentBeginIndex = word.indexOf(START_COMMENT);
        if (word.contains(END_COMMENT)) {
            String secondPartWord = parseEndComment(word);
            word = word.substring(0, commentBeginIndex) + " " + secondPartWord;
        } else {
            word = word.substring(0, commentBeginIndex);
        }
        return word;
    }

    private static String parseEndComment(String word) {
        int commentEndIndex = word.indexOf(END_COMMENT);
        return word.substring(commentEndIndex + 2, word.length());
    }
}
