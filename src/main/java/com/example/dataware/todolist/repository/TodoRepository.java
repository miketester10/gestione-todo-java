package com.example.dataware.todolist.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.dataware.todolist.entity.Todo;
import com.example.dataware.todolist.entity.User;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    Page<Todo> findAllByUser(User user, Pageable pageable);

    Page<Todo> findAllByUserAndCompleted(User user, Boolean completed, Pageable pageable);

    Optional<Todo> findOneByIdAndUser(Long id, User user);

    // @Query(value = "DELETE FROM todos WHERE id = :id AND user_id = :userId",
    // nativeQuery = true)
    // int deleteByIdAndUser(@Param("id") Long id, @Param("userId") Long userId);
}
