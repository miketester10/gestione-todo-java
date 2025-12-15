package com.example.dataware.todolist.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String nome;
    private String email;
    private List<TodoSimpleResponse> todos;
    private Instant createdAt;
    private Instant updatedAt;
}
