package com.example.dataware.todolist.dto.validator;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class UserDto {

    @NotBlank(message = "Il nome è obbligatorio")
    @Size(min = 4, message = "Il nome deve avere almeno 4 caratteri")
    private String nome;

    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "Formato email non valido")
    private String email;

    @NotBlank(message = "La password è obbligatoria")
    @Size(min = 4, message = "La password deve avere almeno 4 caratteri")
    private String password;
}
