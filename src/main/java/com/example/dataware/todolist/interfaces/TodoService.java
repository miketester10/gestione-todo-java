package com.example.dataware.todolist.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import com.example.dataware.todolist.dto.validator.TodoDto;
import com.example.dataware.todolist.dto.validator.TodoUpdateDto;
import com.example.dataware.todolist.entity.Todo;

public interface TodoService {

    Page<Todo> findAll(String email, int page, int limit, Boolean completed, Sort sort);

    Todo findOne(Long todoId, String email);

    Todo create(TodoDto todoDto, String email);

    Todo update(Long todoId, TodoUpdateDto todoUpdateDto, String email);

    void delete(Long todoId, String email);
}
