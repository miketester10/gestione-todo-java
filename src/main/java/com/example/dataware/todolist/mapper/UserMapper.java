package com.example.dataware.todolist.mapper;

import org.mapstruct.Mapper;

import com.example.dataware.todolist.dto.response.UserResponse;
import com.example.dataware.todolist.entity.User;

/**
 * Le implementazioni del codice auto-generato dei mapper si trova in
 * target/generated-sources/annotations
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toDTO(User user);

}
