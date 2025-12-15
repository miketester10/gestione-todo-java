package com.example.dataware.todolist.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dataware.todolist.entity.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {

}
