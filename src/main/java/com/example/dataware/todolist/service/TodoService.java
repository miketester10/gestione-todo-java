package com.example.dataware.todolist.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.dataware.todolist.dto.validator.TodoDto;
import com.example.dataware.todolist.entity.Todo;
import com.example.dataware.todolist.entity.User;
import com.example.dataware.todolist.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // Logger
@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserService userService;

    public List<Todo> findAll(String email) {
        User user = userService.findOne(email);
        return todoRepository.findAllByUser(user);
    }

    public Todo findOne(Long todoId, String email) {
        User user = userService.findOne(email);
        return todoRepository.findOneByIdAndUser(todoId, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo non trovato."));
    }

    public Todo create(TodoDto todoDto, String email) {
        User user = userService.findOne(email);
        Todo todo = Todo.builder()
                .title(todoDto.getTitle())
                .user(user)
                .build();
        return todoRepository.save(todo);
    }

    // TODO PATCH

    public void delete(Long todoId, String email) {
        Todo todo = findOne(todoId, email);
        todoRepository.delete(todo);
    }

}
