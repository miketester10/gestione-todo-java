package com.example.dataware.todolist.mapper;

import org.springframework.stereotype.Component;

import com.example.dataware.todolist.dto.response.TodoSimpleResponse;
import com.example.dataware.todolist.entity.Todo;

@Component
public class TodoMapper {

    public static TodoSimpleResponse toSimpleDTO(Todo todo) {
        TodoSimpleResponse t = TodoSimpleResponse.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .completed(todo.isCompleted())
                .createdAt(todo.getCreatedAt())
                .updatedAt(todo.getUpdatedAt())
                .build();

        return t;
    }

}
