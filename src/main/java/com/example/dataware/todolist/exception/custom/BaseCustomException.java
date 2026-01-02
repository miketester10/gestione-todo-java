package com.example.dataware.todolist.exception.custom;

/**
 * Interfaccia comune per tutte le eccezioni custom dell'applicazione.
 * Definisce i metodi che devono essere implementati da tutte le eccezioni custom
 * per essere gestite correttamente dal GlobalExceptionHandler.
 */
public interface BaseCustomException {
    /**
     * Restituisce il codice di stato HTTP associato all'eccezione.
     * 
     * @return il codice di stato HTTP
     */
    int getStatusCode();

    /**
     * Restituisce la frase di errore HTTP associata all'eccezione.
     * 
     * @return la frase di errore HTTP
     */
    String getErrorReasonPhrase();

    /**
     * Restituisce il messaggio dell'eccezione.
     * 
     * @return il messaggio dell'eccezione
     */
    String getMessage();
}

