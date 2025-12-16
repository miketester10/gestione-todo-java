package com.example.dataware.todolist.dto.validator;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TodoUpdateDto {

    /**
     * Titolo del todo (opzionale)
     * Deve avere almeno 4 caratteri e almeno un carattere non bianco se presente
     */
    @Pattern(regexp = ".*\\S.*", message = "Il titolo non può essere vuoto o solo spazi")
    @Size(min = 4, message = "Il titolo deve avere almeno 4 caratteri")
    private String title;

    /**
     * Non esiste un decoratore dedicato come @IsBoolean() in NestJS
     * Tipizzandolo come Boolean proverà a trasformare in automatico
     * un numero in valore booleano (0 -> false, 1+ -> true),
     * oppure la stringa "true" -> true e "false" -> false,
     * oppure null -> false
     * Nel caso viene inviato un valore non valido, per esempio, la stringa "ciao",
     * lancierà l'eccezione HttpMessageNotReadableException,
     * perchè non riesce a deserializzarlo.
     * La suddetta eccezzione viene catturata dal GlobalExceptionHandler
     * (vedere file GlobalExceptionHandler.java)
     */
    private Boolean completed;
}
