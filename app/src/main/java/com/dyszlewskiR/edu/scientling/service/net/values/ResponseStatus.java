package com.dyszlewskiR.edu.scientling.service.net.values;

/**
 * Created by Razjelll on 21.04.2017.
 */

public class ResponseStatus {
    /**Zawartość żądanego doklumentu*/
    public static final int OK = 200;
    /**Utworzono - wysłany dokument został zapisany na serwerze*/
    public static final int CREATED = 201;
    /**Przyjęto  - zapytanie zostało przyjęte do obsłużenia, lecz jego zrealizowanie jeszcze się nie skończyło*/
    public static final int ACCEPTED = 202;
    /**Brak zawartości - serwer zrealizował zapytanie klienta i nie potrzebuje zwracać żadnej treści*/
    public static final int NO_CONTENT = 204;



    /**Nieprawidłowe zapytanie - błąd użytkownika(np. błędna składnia zapytania*/
    public static final int BAD_REQUEST = 400;
    /**Nieautoryzowany dostęp - żądanie zasobu który wymaga uwierzytelnienia*/
    public static final int UNAUTHORIZED = 401;
    /**Nie znaleziono - serwer nie odnalazł zasobu według podanego URL */
    public static final int NOT_FOUND = 404;
    /**Koniec czasu oczekiwania na żądanie - klient nie przesłał zapytania do serwera w określonym czasie*/
    public static final int REQUEST_TIMEOUT = 408;
    /**Warunek wstępny nie może być spełniony*/
    public static final int PRECONDITION_FAILED = 412;
}
