package com.example.dataware.todolist.dto.response.builder;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonPropertyOrder({ "statusCode", "message", "data", "timestamp" }) // Mantiene l'ordine delle proprietà nella risposta
                                                                     // così come viene definita
@Data // Genera getter e setter automatici per il DTO
@Builder // Builder pattern per creare istanze in modo leggibile e flessibile
@NoArgsConstructor // Costruttore vuoto (utile per la deserializzazione JSON)
@AllArgsConstructor // Costruttore pieno (utile per i test)
public class SuccessResponse<T> {
    private int statusCode;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL) // Non serializza il campo se è null
    private T data;

    @Builder.Default
    private Instant timestamp = Instant.now(); // formato UTC

}
