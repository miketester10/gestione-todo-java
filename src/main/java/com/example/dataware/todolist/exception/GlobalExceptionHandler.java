package com.example.dataware.todolist.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

/**
 * Gestore globale delle eccezioni native e di framework.
 * Intercetta le eccezioni Java/Spring non custom
 * (validazione, parsing, autorizzazione, multipart, ecc.)
 * e restituisce risposte JSON standardizzate senza includere lo stack trace.
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE) // Global/fallback handler → gestisce tutto ciò che non è catturato prima
public class GlobalExceptionHandler {

        @Value("${spring.servlet.multipart.max-file-size}")
        private String MAX_SIZE;

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
                String reasonPhrase = httpStatus.getReasonPhrase();
                String message = ex.getReason();

                return handleException(ex, statusCode, reasonPhrase, message);
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
                String reasonPhrase = HttpStatus.BAD_REQUEST.getReasonPhrase();

                Map<String, String> validationErrors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach(e -> {
                        String fieldName = ((FieldError) e).getField();
                        String errorMessage = e.getDefaultMessage();
                        validationErrors.put(fieldName, errorMessage);
                });

                return handleException(ex, statusCode, reasonPhrase,
                                validationErrors);
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
                String reasonPhrase = HttpStatus.BAD_REQUEST.getReasonPhrase();

                return handleException(ex, statusCode, reasonPhrase,
                                "Devi inviare un body valido insieme alla richiesta HTTP");
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
                String reasonPhrase = HttpStatus.BAD_REQUEST.getReasonPhrase();

                String message = "Parametro [" + ex.getName()
                                + "] non valido nel percorso URL. Assicurati di utilizzare il formato corretto.";

                return handleException(ex, statusCode, reasonPhrase, message);
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
                String reasonPhrase = HttpStatus.FORBIDDEN.getReasonPhrase();

                return handleException(ex, statusCode, reasonPhrase,
                                "Accesso negato. Non hai i permessi necessari per eseguire questa operazione.");
        }

        /**
         * Gestisce le eccezioni MultipartException lanciate
         * dall'applicazione.
         * Restituisce una risposta JSON pulita senza stack trace.
         * 
         * @param ex l'eccezione MultipartException
         * @return ResponseEntity con la risposta di errore formattata
         */
        @ExceptionHandler(MultipartException.class)
        public ResponseEntity<ErrorResponse> handleMultipartException(MultipartException ex) {
                int statusCode = HttpStatus.BAD_REQUEST.value();
                String reasonPhrase = HttpStatus.BAD_REQUEST.getReasonPhrase();

                return handleException(ex, statusCode, reasonPhrase, ex.getMessage());
        }

        /**
         * Gestisce le eccezioni MissingServletRequestPartException lanciate
         * dall'applicazione.
         * Restituisce una risposta JSON pulita senza stack trace.
         * 
         * @param ex l'eccezione MissingServletRequestPartException
         * @return ResponseEntity con la risposta di errore formattata
         */
        @ExceptionHandler(MissingServletRequestPartException.class)
        public ResponseEntity<ErrorResponse> handleMissingServletRequestPartException(
                        MissingServletRequestPartException ex) {
                int statusCode = HttpStatus.BAD_REQUEST.value();
                String reasonPhrase = HttpStatus.BAD_REQUEST.getReasonPhrase();

                return handleException(ex, statusCode, reasonPhrase,
                                ex.getMessage());
        }

        /**
         * Gestisce le eccezioni MaxUploadSizeExceededException lanciate
         * dall'applicazione.
         * Restituisce una risposta JSON pulita senza stack trace.
         * 
         * @param ex l'eccezione MaxUploadSizeExceededException
         * @return ResponseEntity con la risposta di errore formattata
         */
        @ExceptionHandler(MaxUploadSizeExceededException.class)
        public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
                int statusCode = HttpStatus.BAD_REQUEST.value();
                String reasonPhrase = HttpStatus.BAD_REQUEST.getReasonPhrase();

                String message = "Il file supera la dimensione massima consentita: " + MAX_SIZE;

                return handleException(ex, statusCode, reasonPhrase, message);
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
                String reasonPhrase = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();

                return handleException(ex, statusCode, reasonPhrase,
                                "Si è verificato un errore interno del server");
        }

        /**
         * Metodo helper per gestire tutte le eccezioni native/Spring.
         * Centralizza la logica di logging e costruzione della risposta.
         * Estrae automaticamente il nome della classe dell'eccezione per il logging.
         * 
         * @param ex           l'istanza dell'eccezione
         * @param statusCode   il codice di stato HTTP
         * @param reasonPhrase la frase di errore HTTP
         * @param message      il messaggio di errore (può essere String o Object)
         * @return ResponseEntity con la risposta di errore formattata
         */
        private ResponseEntity<ErrorResponse> handleException(
                        Exception ex, int statusCode, String reasonPhrase, Object message) {
                String exceptionName = ex.getClass().getSimpleName();

                log.error("{}: {} - {}", exceptionName, statusCode, message);

                return ErrorResponse.buildResponse(statusCode, reasonPhrase, message);
        }
}