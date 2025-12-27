package com.example.dataware.todolist.mapper;

import org.mapstruct.Mapper;

import com.example.dataware.todolist.dto.response.TodoResponse;
import com.example.dataware.todolist.entity.Todo;

/**
 * Le implementazioni del codice auto-generato dei mapper si trova in
 * target/generated-sources/annotations
 */
@Mapper(componentModel = "spring")
public interface TodoMapper {

    TodoResponse toDTO(Todo todo);

}
