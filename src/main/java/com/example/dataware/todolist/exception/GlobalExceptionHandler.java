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

import com.example.dataware.todolist.exception.custom.BaseCustomException;
import com.example.dataware.todolist.exception.custom.EmailConflictException;
import com.example.dataware.todolist.exception.custom.InvalidCredentialsException;
import com.example.dataware.todolist.exception.custom.InvalidSortablePropertyException;
import com.example.dataware.todolist.exception.custom.TodoNotFoundException;
import com.example.dataware.todolist.exception.custom.UserNotFoundException;

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
         * Gestisce le eccezioni EmailConflictException lanciate
         * dall'applicazione.
         * Restituisce una risposta JSON pulita senza stack trace.
         * 
         * @param ex l'eccezione EmailConflictException
         * @return ResponseEntity con la risposta di errore formattata
         */
        @ExceptionHandler(EmailConflictException.class)
        public ResponseEntity<ErrorResponse> handleEmailConflictException(EmailConflictException ex) {
                return handleCustomException(ex, EmailConflictException.class.getSimpleName());
        }

        /**
         * Gestisce le eccezioni InvalidCredentialsException lanciate
         * dall'applicazione.
         * Restituisce una risposta JSON pulita senza stack trace.
         * 
         * @param ex l'eccezione InvalidCredentialsException
         * @return ResponseEntity con la risposta di errore formattata
         */
        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException ex) {
                return handleCustomException(ex, InvalidCredentialsException.class.getSimpleName());
        }

        /**
         * Gestisce le eccezioni UserNotFoundException lanciate
         * dall'applicazione.
         * Restituisce una risposta JSON pulita senza stack trace.
         * 
         * @param ex l'eccezione UserNotFoundException
         * @return ResponseEntity con la risposta di errore formattata
         */
        @ExceptionHandler(UserNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
                return handleCustomException(ex, UserNotFoundException.class.getSimpleName());
        }

        /**
         * Gestisce le eccezioni TodoNotFoundException lanciate
         * dall'applicazione.
         * Restituisce una risposta JSON pulita senza stack trace.
         * 
         * @param ex l'eccezione TodoNotFoundException
         * @return ResponseEntity con la risposta di errore formattata
         */
        @ExceptionHandler(TodoNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleTodoNotFoundException(TodoNotFoundException ex) {
                return handleCustomException(ex, TodoNotFoundException.class.getSimpleName());
        }

        /**
         * Gestisce le eccezioni InvalidSortablePropertyException lanciate
         * dall'applicazione.
         * Restituisce una risposta JSON pulita senza stack trace.
         * 
         * @param ex l'eccezione InvalidSortablePropertyException
         * @return ResponseEntity con la risposta di errore formattata
         */
        @ExceptionHandler(InvalidSortablePropertyException.class)
        public ResponseEntity<ErrorResponse> handleInvalidSortablePropertyException(
                        InvalidSortablePropertyException ex) {
                return handleCustomException(ex, InvalidSortablePropertyException.class.getSimpleName());
        }

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

                log.error("{}: {} - {}", ResponseStatusException.class.getSimpleName(), statusCode, message);

                return buildErrorResponse(statusCode, errorPhrase, message);
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

                log.error("{}: {} - {}", MethodArgumentNotValidException.class.getSimpleName(), statusCode,
                                validationErrors);

                return buildErrorResponse(statusCode, error, validationErrors);
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

                log.error("{}: {} - {}", HttpMessageNotReadableException.class.getSimpleName(), statusCode, error);

                return buildErrorResponse(statusCode, error, "Devi inviare un body valido insieme alla richiesta HTTP");
        }

        /**
         * Gestisce le eccezioni MethodArgumentTypeMismatchException quando non viene
         * passato il tipo corretto come @PathVariable oppure come @RequestParam.
         * Restituisce un messaggio di errore dettagliato con i campi non validi.
         * 
         * @param ex l'eccezione MethodArgumentTypeMismatchException
         * @return ResponseEntity con la risposta di errore formattata
         */
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ErrorResponse> handleTypeMismatch(
                        MethodArgumentTypeMismatchException ex) {
                int statusCode = HttpStatus.BAD_REQUEST.value();
                String error = HttpStatus.BAD_REQUEST.getReasonPhrase();

                log.error("{}: {} - {}", MethodArgumentTypeMismatchException.class.getSimpleName(), statusCode, error);

                String message = "Parametro [" + ex.getName()
                                + "] non valido nel percorso URL. Assicurati di utilizzare il formato corretto.";

                return buildErrorResponse(statusCode, error, message);
        }

        /**
         * Gestisce le eccezioni di autorizzazione negate
         * (ad esempio non si hanno ruoli necessari).
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

                log.error("{}: {} - {}", AuthorizationDeniedException.class.getSimpleName(), statusCode, error);

                return buildErrorResponse(statusCode, error,
                                "Accesso negato. Non hai i permessi necessari per eseguire questa operazione.");
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

                log.error("Errore generico: {}", ex.getMessage());

                return buildErrorResponse(statusCode, error, "Si è verificato un errore interno del server");
        }

        /**
         * Metodo helper per gestire tutte le eccezioni custom che implementano
         * BaseCustomException.
         * 
         * @param ex            l'eccezione custom
         * @param exceptionName il nome dell'eccezione per il logging
         * @return ResponseEntity con la risposta di errore formattata
         */
        private ResponseEntity<ErrorResponse> handleCustomException(BaseCustomException ex, String exceptionName) {
                log.error("{}: {} - {}", exceptionName, ex.getStatusCode(), ex.getMessage());

                return buildErrorResponse(ex.getStatusCode(), ex.getErrorReasonPhrase(), ex.getMessage());
        }

        /**
         * Metodo helper per costruire una ResponseEntity con ErrorResponse.
         * 
         * @param statusCode il codice di stato HTTP
         * @param error      la frase di errore HTTP
         * @param message    il messaggio di errore (può essere String o Object)
         * @return ResponseEntity con la risposta di errore formattata
         */
        private ResponseEntity<ErrorResponse> buildErrorResponse(int statusCode, String error, Object message) {
                ErrorResponse errorResponse = ErrorResponse.builder()
                                .statusCode(statusCode)
                                .error(error)
                                .message(message)
                                .build();

                return ResponseEntity.status(statusCode).body(errorResponse);
        }
}
