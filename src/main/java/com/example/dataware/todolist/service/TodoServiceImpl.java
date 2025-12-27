package com.example.dataware.todolist.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.dataware.todolist.dto.validator.TodoDto;
import com.example.dataware.todolist.dto.validator.TodoUpdateDto;
import com.example.dataware.todolist.entity.Todo;
import com.example.dataware.todolist.entity.User;
import com.example.dataware.todolist.interfaces.TodoService;
import com.example.dataware.todolist.interfaces.UserService;
import com.example.dataware.todolist.repository.TodoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // Logger
@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final UserService userService;

    @Override
    public Page<Todo> findAll(String email, int page, int limit, Boolean completed) {
        User user = userService.findOne(email);
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "updatedAt"));
        if (completed == null) {
            return todoRepository.findAllByUser(user, pageable);
        }
        return todoRepository.findAllByUserAndCompleted(user, completed, pageable);
    }

    @Override
    public Todo findOne(Long todoId, String email) {
        User user = userService.findOne(email);
        return todoRepository.findOneByIdAndUser(todoId, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo non trovato."));
    }

    @Override
    public Todo create(TodoDto todoDto, String email) {
        User user = userService.findOne(email);
        Todo todo = Todo.builder()
                .title(todoDto.getTitle())
                .user(user)
                .build();
        return todoRepository.save(todo);
    }

    @Override
    public Todo update(Long todoId, TodoUpdateDto todoUpdateDto, String email) {
        Todo todo = findOne(todoId, email);

        // Aggiorna solo i campi presenti nel DTO
        if (todoUpdateDto.getTitle() != null) {
            todo.setTitle(todoUpdateDto.getTitle());
        }

        if (todoUpdateDto.getCompleted() != null) {
            todo.setCompleted(todoUpdateDto.getCompleted());
        }

        return todoRepository.save(todo);
    }

    @Override
    public void delete(Long todoId, String email) {
        Todo todo = findOne(todoId, email);
        todoRepository.delete(todo);
    }

}
