package com.example.dataware.todolist.mapper;

import org.mapstruct.Mapper;

import com.example.dataware.todolist.dto.response.TodoResponse;
import com.example.dataware.todolist.dto.response.TodoSimpleResponse;
import com.example.dataware.todolist.entity.Todo;

/**
 * Le implementazioni del codice auto-generato dei mapper si trova in
 * target/generated-sources/annotations
 */
@Mapper(componentModel = "spring")
public interface TodoMapper {

    TodoSimpleResponse toSimpleDTO(Todo todo);

    TodoResponse toDTO(Todo todo);

    /**
     * Mapper manuale senza mapstruct-processor @Mapper(componentModel = "spring")
     */
    // public static TodoSimpleResponse toSimpleDTO(Todo todo) {
    // TodoSimpleResponse t = TodoSimpleResponse.builder()
    // .id(todo.getId())
    // .title(todo.getTitle())
    // .completed(todo.isCompleted())
    // .createdAt(todo.getCreatedAt())
    // .updatedAt(todo.getUpdatedAt())
    // .build();

    // return t;
    // }

    // public TodoResponse toDTO(Todo todo) {
    // TodoResponse t = TodoResponse.builder()
    // .id(todo.getId())
    // .title(todo.getTitle())
    // .completed(todo.isCompleted())
    // .createdAt(todo.getCreatedAt())
    // .updatedAt(todo.getUpdatedAt())
    // .user(UserMapper.toSimpleDTO(todo.getUser()))
    // .build();

    // return t;
    // }
}
