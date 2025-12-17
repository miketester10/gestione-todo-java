package com.example.dataware.todolist.mapper;

import org.mapstruct.Mapper;

import com.example.dataware.todolist.dto.response.UserResponse;
import com.example.dataware.todolist.dto.response.UserSimpleResponse;
import com.example.dataware.todolist.entity.User;

/**
 * Le implementazioni del codice auto-generato dei mapper si trova in
 * target/generated-sources/annotations
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserSimpleResponse toSimpleDTO(User user);

    UserResponse toDTO(User user);

    /**
     * Mapper manuale senza mapstruct-processor @Mapper(componentModel = "spring")
     */
    // public static UserSimpleResponse toSimpleDTO(User user) {
    // UserSimpleResponse u = UserSimpleResponse.builder()
    // .id(user.getId())
    // .nome(user.getNome())
    // .email(user.getEmail())
    // .createdAt(user.getCreatedAt())
    // .updatedAt(user.getUpdatedAt())
    // .build();

    // return u;
    // }

    // public UserResponse toDTO(User user) {
    // UserResponse u = UserResponse.builder()
    // .id(user.getId())
    // .nome(user.getNome())
    // .email(user.getEmail())
    // .createdAt(user.getCreatedAt())
    // .updatedAt(user.getUpdatedAt())
    // .todos(
    // user
    // .getTodos()
    // .stream()
    // .map(todo -> TodoMapper.toSimpleDTO(todo))
    // .toList())
    // .build();

    // return u;
    // }
}
