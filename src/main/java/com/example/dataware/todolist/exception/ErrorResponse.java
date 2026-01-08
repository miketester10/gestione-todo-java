package com.example.dataware.todolist.exception;

import java.time.Instant;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonPropertyOrder({ "statusCode", "reason", "message", "timestamp" }) // Mantiene l'ordine delle proprietà nella
                                                                       // risposta così come viene definita
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int statusCode;
    private String reason;
    private Object message;

    @Builder.Default
    private Instant timestamp = Instant.now(); // formato UTC

    /**
     * Metodo helper statico per costruire una ResponseEntity con ErrorResponse.
     * 
     * @param statusCode   il codice di stato HTTP
     * @param reasonPhrase la frase di errore HTTP
     * @param message      il messaggio di errore (può essere String o Object)
     * @return ResponseEntity con la risposta di errore formattata
     */
    public static ResponseEntity<ErrorResponse> buildResponse(int statusCode, String reasonPhrase, Object message) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(statusCode)
                .reason(reasonPhrase)
                .message(message)
                .build();

        return ResponseEntity.status(statusCode).body(errorResponse);
    }
}
