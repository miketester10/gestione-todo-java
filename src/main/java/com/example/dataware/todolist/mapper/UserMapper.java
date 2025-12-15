package com.example.dataware.todolist.mapper;

import org.springframework.stereotype.Component;

import com.example.dataware.todolist.dto.response.UserResponse;
import com.example.dataware.todolist.entity.User;

@Component
public class UserMapper {

    public UserResponse toDTO(User user) {
        UserResponse u = UserResponse.builder()
                .id(user.getId())
                .nome(user.getNome())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .todos(
                        user
                                .getTodos()
                                .stream()
                                .map(todo -> TodoMapper.toSimpleDTO(todo))
                                .toList())
                .build();

        return u;
    }

}
