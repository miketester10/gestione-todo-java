package com.example.dataware.todolist.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
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
     * Gestisce le eccezioni HttpMessageNotReadableException, ad esempio,
     * quando non viene inviato un body in una richiesta POST/PUT/PATCH,
     * oppure viene inviato un valore che non riesce a deserializzare.
     * Restituisce una risposta preimpostata.
     * 
     * @param ex l'eccezione HttpMessageNotReadableException
     * @return ResponseEntity con la risposta di errore formattata
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {

        int statusCode = HttpStatus.BAD_REQUEST.value();
        String error = HttpStatus.BAD_REQUEST.getReasonPhrase();

        log.error("Errore HttpMessageNotReadable: {} - {}", statusCode, error);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(statusCode)
                .error(error)
                .message("Devi inviare un body valido insieme alla richiesta HTTP")
                .build();

        return ResponseEntity.status(statusCode).body(errorResponse);
    }

    /**
     * Gestisce le eccezioni MethodArgumentTypeMismatchException quando non viene
     * passato un numero intero come @PathVariable.
     * Restituisce una risposta preimpostata.
     * 
     * @param ex l'eccezione MethodArgumentTypeMismatchException
     * @return ResponseEntity con la risposta di errore formattata
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        int statusCode = HttpStatus.BAD_REQUEST.value();
        String error = HttpStatus.BAD_REQUEST.getReasonPhrase();

        log.error("Errore TypeMismatch: {} - {}", statusCode, error);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(statusCode)
                .error(error)
                .message("L'id deve essere un numero intero positivo")
                .build();

        return ResponseEntity.status(statusCode).body(errorResponse);
    }

    /**
     * Gestisce le eccezioni di autorizzazione negate (ad esempio non si hanno i ruoli necessari).
     * Restituisce una risposta di errore con stato 403 Forbidden.
     * 
     * @param ex l'eccezione AuthorizationDeniedException
     * @return ResponseEntity con la risposta di errore formattata
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDenied(
            AuthorizationDeniedException ex) {

        int statusCode = HttpStatus.FORBIDDEN.value();
        String error = HttpStatus.FORBIDDEN.getReasonPhrase();

        log.error("Errore AuthorizationDenied: {} - {}", statusCode, error);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(statusCode)
                .error(error)
                .message("Accesso negato. Non hai i permessi necessari per eseguire questa operazione.")
                .build();

        return ResponseEntity.status(statusCode).body(errorResponse);
    }

    /**
     * Gestisce tutte le altre eccezioni non gestite.
     * Restituisce una risposta generica di errore interno del server preimpostata.
     * 
     * @param ex l'eccezione generica
     * @return ResponseEntity con la risposta di errore formattata
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {

        int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String error = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();

        log.error("Errore non gestito: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(statusCode)
                .error(error)
                .message("Si è verificato un errore interno del server")
                .build();

        return ResponseEntity.status(statusCode).body(errorResponse);
    }
}
