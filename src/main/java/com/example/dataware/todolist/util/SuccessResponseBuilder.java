package com.example.dataware.todolist.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class SuccessResponseBuilder {

    /**
     * Metodo principale: successo con dati e messaggio opzionale
     */
    public <T> ResponseEntity<SuccessResponse<T>> success(T data, HttpStatus status, String message) {

        int statusCode = status.value();
        String finalMessage = (message != null) ? message : "Success";

        SuccessResponse<T> response = SuccessResponse.<T>builder()
                .statusCode(statusCode)
                .message(finalMessage)
                .data(data)
                .build();

        return ResponseEntity.status(status).body(response);
    }

    /**
     * Overload: successo con dati, senza messaggio personalizzato
     */
    public <T> ResponseEntity<SuccessResponse<T>> success(T data, HttpStatus status) {
        return success(data, status, null);
    }

    /**
     * Overload: successo solo con HttpStatus
     */
    public ResponseEntity<SuccessResponse<Void>> success(HttpStatus status) {
        return success(null, status, null);
    }
}
