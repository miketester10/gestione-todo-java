package com.example.dataware.todolist.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.dataware.todolist.dto.response.TodoResponse;
import com.example.dataware.todolist.dto.validator.TodoDto;
import com.example.dataware.todolist.entity.Todo;
import com.example.dataware.todolist.jwt.JwtPayload;
import com.example.dataware.todolist.mapper.TodoMapper;
import com.example.dataware.todolist.service.TodoService;
import com.example.dataware.todolist.util.SuccessResponse;
import com.example.dataware.todolist.util.SuccessResponseBuilder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // Logger
@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;
    private final TodoMapper todoMapper;
    private final SuccessResponseBuilder apiResponseBuilder;

    @GetMapping()
    public ResponseEntity<SuccessResponse<List<TodoResponse>>> findAll(@AuthenticationPrincipal JwtPayload jwtPayload) {
        List<Todo> todos = todoService.findAll(jwtPayload.getEmail());
        List<TodoResponse> todoResponse = todos
                .stream()
                .map(todo -> todoMapper.toDTO(todo))
                .toList();
        return apiResponseBuilder.success(todoResponse, HttpStatus.OK);
    }

    @GetMapping("/{todoId}")
    public ResponseEntity<SuccessResponse<TodoResponse>> findOne(@AuthenticationPrincipal JwtPayload jwtPayload,
            @PathVariable Long todoId) {
        Todo todo = todoService.findOne(todoId, jwtPayload.getEmail());
        TodoResponse userResponse = todoMapper.toDTO(todo);
        return apiResponseBuilder.success(userResponse, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<TodoResponse>> create(@AuthenticationPrincipal JwtPayload jwtPayload,
            @Valid @RequestBody TodoDto todoDto) {
        Todo todo = todoService.create(todoDto, jwtPayload.getEmail());
        TodoResponse studenteResponse = todoMapper.toDTO(todo);
        return apiResponseBuilder.success(studenteResponse, HttpStatus.CREATED);
    }

    // TODO PATCH

    @DeleteMapping("/{todoId}")
    public ResponseEntity<SuccessResponse<Void>> delete(@AuthenticationPrincipal JwtPayload jwtPayload,
            @PathVariable Long todoId) {
        todoService.delete(todoId, jwtPayload.getEmail());
        return apiResponseBuilder.success(HttpStatus.OK);
    }

}
