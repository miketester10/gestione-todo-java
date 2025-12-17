package com.example.dataware.todolist.interfaces;

import java.util.List;

import com.example.dataware.todolist.dto.validator.TodoDto;
import com.example.dataware.todolist.dto.validator.TodoUpdateDto;
import com.example.dataware.todolist.entity.Todo;

public interface TodoService {

    List<Todo> findAll(String email);

    Todo findOne(Long todoId, String email);

    Todo create(TodoDto todoDto, String email);

    Todo update(Long todoId, TodoUpdateDto todoUpdateDto, String email);

    void delete(Long todoId, String email);
}
