package com.example.dataware.todolist.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

/**
 * Gestore globale delle eccezioni per l'applicazione.
 * Intercetta tutte le eccezioni e restituisce risposte JSON standardizzate
 * senza includere lo stack trace.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gestisce le ResponseStatusException lanciate dall'applicazione.
     * Restituisce una risposta JSON pulita senza stack trace.
     * 
     * @param ex l'eccezione ResponseStatusException
     * @return ResponseEntity con la risposta di errore formattata
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {

        int statusCode = ex.getStatusCode().value();
        HttpStatus httpStatus = HttpStatus.resolve(statusCode);
        String errorPhrase = httpStatus.getReasonPhrase();
        String message = ex.getReason();

        log.error("ResponseStatusException: {} - {}", statusCode, message);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(statusCode)
                .error(errorPhrase)
                .message(message)
                .build();

        return ResponseEntity.status(statusCode).body(errorResponse);
    }

    /**
     * Gestisce le eccezioni di validazione dei DTO.
     * Restituisce un messaggio di errore dettagliato con i campi non validi.
     * 
     * @param ex l'eccezione MethodArgumentNotValidException
     * @return ResponseEntity con la risposta di errore formattata
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {

        int statusCode = HttpStatus.BAD_REQUEST.value();
        String error = HttpStatus.BAD_REQUEST.getReasonPhrase();

        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(e -> {
            String fieldName = ((FieldError) e).getField();
            String errorMessage = e.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        log.error("Errore di validazione: {} - {}", statusCode, validationErrors);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(statusCode)
                .error(error)
                .message(validationErrors)
                .build();

        return ResponseEntity.status(statusCode).body(errorResponse);
    }

    /**
     * Gestisce tutte le altre eccezioni non gestite.
     * Restituisce una risposta generica di errore interno del server.
     * 
     * @param ex l'eccezione generica
     * @return ResponseEntity con la risposta di errore formattata
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {

        int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String error = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();

        log.error("Errore non gestito: ", ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(statusCode)
                .error(error)
                .message("Si è verificato un errore interno del server")
                .build();

        return ResponseEntity.status(statusCode).body(errorResponse);
    }
}
