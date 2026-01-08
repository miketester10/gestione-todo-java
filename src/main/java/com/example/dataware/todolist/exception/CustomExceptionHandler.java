package com.example.dataware.todolist.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.dataware.todolist.exception.custom.BaseCustomException;
import com.example.dataware.todolist.exception.custom.EmailConflictException;
import com.example.dataware.todolist.exception.custom.EmptyFileException;
import com.example.dataware.todolist.exception.custom.InvalidCredentialsException;
import com.example.dataware.todolist.exception.custom.InvalidFileTypeException;
import com.example.dataware.todolist.exception.custom.InvalidSortablePropertyException;
import com.example.dataware.todolist.exception.custom.S3UploadException;
import com.example.dataware.todolist.exception.custom.TodoNotFoundException;
import com.example.dataware.todolist.exception.custom.UserNotFoundException;

import lombok.extern.slf4j.Slf4j;

/**
 * Gestore delle eccezioni custom dell'applicazione.
 * Intercetta esclusivamente le eccezioni definite dall'applicazione
 * (estendono BaseCustomException) e restituisce risposte JSON
 * standardizzate senza includere lo stack trace.
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE) // Custom exceptions handler → deve essere valutato prima di tutto
public class CustomExceptionHandler {

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
        return handleException(ex);
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
        return handleException(ex);
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
        return handleException(ex);
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
        return handleException(ex);
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
        return handleException(ex);
    }

    /**
     * Gestisce le eccezioni EmptyFileException lanciate
     * dall'applicazione.
     * Restituisce una risposta JSON pulita senza stack trace.
     * 
     * @param ex l'eccezione EmptyFileException
     * @return ResponseEntity con la risposta di errore formattata
     */
    @ExceptionHandler(EmptyFileException.class)
    public ResponseEntity<ErrorResponse> handleEmptyFileException(
            EmptyFileException ex) {
        return handleException(ex);
    }

    /**
     * Gestisce le eccezioni InvalidFileTypeException lanciate
     * dall'applicazione.
     * Restituisce una risposta JSON pulita senza stack trace.
     * 
     * @param ex l'eccezione InvalidFileTypeException
     * @return ResponseEntity con la risposta di errore formattata
     */
    @ExceptionHandler(InvalidFileTypeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFileTypeException(
            InvalidFileTypeException ex) {
        return handleException(ex);
    }

    /**
     * Gestisce le eccezioni S3UploadException lanciate
     * dall'applicazione.
     * Restituisce una risposta JSON pulita senza stack trace.
     * 
     * @param ex l'eccezione S3UploadException
     * @return ResponseEntity con la risposta di errore formattata
     */
    @ExceptionHandler(S3UploadException.class)
    public ResponseEntity<ErrorResponse> handleS3UploadException(
            S3UploadException ex) {
        return handleException(ex);
    }

    /**
     * Metodo helper per gestire tutte le eccezioni custom che implementano
     * BaseCustomException.
     * Centralizza la logica di logging e costruzione della risposta.
     * Estrae automaticamente il nome della classe dell'eccezione per il logging.
     * 
     * @param ex l'istanza dell'eccezione custom
     * @return ResponseEntity con la risposta di errore formattata
     */
    private ResponseEntity<ErrorResponse> handleException(BaseCustomException ex) {
        String exceptionName = ex.getClass().getSimpleName();

        log.error("{}: {} - {}", exceptionName, ex.getStatusCode(), ex.getMessage());

        return ErrorResponse.buildResponse(ex.getStatusCode(), ex.getErrorReasonPhrase(), ex.getMessage());
    }

}
