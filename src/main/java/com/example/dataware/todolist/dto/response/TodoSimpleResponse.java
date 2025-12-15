package com.example.dataware.todolist.dto.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodoSimpleResponse {
    private Long id;
    private String title;
    private Boolean completed;
    private Instant createdAt;
    private Instant updatedAt;
}
