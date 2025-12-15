package com.example.dataware.todolist.dto.validator;

import jakarta.validation.constraints.NotBlank; // Validazione NotBlank
import jakarta.validation.constraints.Size; // Validazione Size
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// @NotBlank
// ""        → invalid
// "   "     → invalid
// null      → invalid
// "abc"     → valid

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodoDto {

    @NotBlank(message = "Il titolo è obbligatorio")
    @Size(min = 4, message = "Il titolo deve avere almeno 4 caratteri")
    private String title;
}
