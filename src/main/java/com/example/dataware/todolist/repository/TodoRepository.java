package com.example.dataware.todolist.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.dataware.todolist.entity.Todo;
import com.example.dataware.todolist.entity.User;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByUser(User user);

    Optional<Todo> findOneByIdAndUser(Long id, User user);

    // @Query(value = "DELETE FROM todos WHERE id = :id AND user_id = :userId",
    // nativeQuery = true)
    // int deleteByIdAndUser(@Param("id") Long id, @Param("userId") Long userId);
}
