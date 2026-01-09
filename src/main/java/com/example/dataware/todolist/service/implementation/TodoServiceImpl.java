package com.example.dataware.todolist.service.implementation;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.dataware.todolist.dto.validator.TodoDto;
import com.example.dataware.todolist.dto.validator.TodoUpdateDto;
import com.example.dataware.todolist.entity.Todo;
import com.example.dataware.todolist.entity.User;
import com.example.dataware.todolist.exception.custom.InvalidSortablePropertyException;
import com.example.dataware.todolist.exception.custom.TodoNotFoundException;
import com.example.dataware.todolist.repository.TodoRepository;
import com.example.dataware.todolist.service.interfaces.TodoService;
import com.example.dataware.todolist.service.interfaces.UserService;
import com.example.dataware.todolist.util.sort.TodoSortableProperty;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // Logger
@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true) // Ottimizza le performance per le operazioni di lettura
    public Page<Todo> findAll(String email, int page, int limit, Boolean completed, Sort sort) {
        User user = userService.findOne(email);

        // fallback automatico se sort null o vuoto
        // (esempio: /todos oppure /todos?sort=<empty>)
        if (sort == null || !sort.isSorted()) {
            sort = Sort.by("updatedAt").descending();
        } else {
            // validazione proprietà non valide
            List<String> invalidSortableProperty = sort.stream()
                    .map(order -> order.getProperty())
                    .filter(property -> !TodoSortableProperty.isValid(property))
                    .toList();

            if (!invalidSortableProperty.isEmpty()) {
                throw new InvalidSortablePropertyException(
                        "Proprietà di ordinamento non valide: " + String.join(", ", invalidSortableProperty));
            }
        }

        Pageable pageable = PageRequest.of(page, limit, sort);

        // applica filtro completed se presente
        if (completed == null) {
            return todoRepository.findAllByUser(user, pageable);
        }
        return todoRepository.findAllByUserAndCompleted(user, completed, pageable);
    }

    @Override
    @Transactional(readOnly = true) // Lettura sicura senza lock in scrittura
    public Todo findOne(Long todoId, String email) {
        User user = userService.findOne(email);
        return todoRepository.findOneByIdAndUser(todoId, user)
                .orElseThrow(() -> new TodoNotFoundException("Todo non trovato."));
    }

    @Override
    @Transactional // Unica transazione per salvataggio e associazioni
    public Todo create(TodoDto todoDto, String email) {
        User user = userService.findOne(email);
        Todo todo = Todo.builder()
                .title(todoDto.getTitle())
                .user(user)
                .build();
        return todoRepository.save(todo);
    }

    @Override
    @Transactional // Garantisce che il fetch e l'update avvengano nella stessa transazione
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
    @Transactional // Assicura che l'eliminazione sia atomica
    public void delete(Long todoId, String email) {
        Todo todo = findOne(todoId, email);
        todoRepository.delete(todo);
    }

}
