package com.example.dataware.todolist.exception;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonPropertyOrder({ "statusCode", "error", "message", "timestamp" }) // Mantiene l'ordine delle proprietà nella
                                                                      // risposta così come viene definita
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int statusCode;
    private String error;
    private Object message;

    @Builder.Default
    private Instant timestamp = Instant.now(); // formato UTC
}
