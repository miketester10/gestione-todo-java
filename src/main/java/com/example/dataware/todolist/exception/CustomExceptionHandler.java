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
        return handleCustomException(ex, EmptyFileException.class.getSimpleName());
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
        return handleCustomException(ex, InvalidFileTypeException.class.getSimpleName());
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
        return handleCustomException(ex, S3UploadException.class.getSimpleName());
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

        return ErrorResponse.buildResponse(ex.getStatusCode(), ex.getErrorReasonPhrase(), ex.getMessage());
    }

}
