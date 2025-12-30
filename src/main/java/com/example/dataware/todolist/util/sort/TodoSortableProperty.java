package com.example.dataware.todolist.util.sort;

import java.util.Arrays;

public enum TodoSortableProperty {
    id, title, completed, createdAt, updatedAt;

    public static boolean isValid(String property) {
        return Arrays.stream(TodoSortableProperty.values())
                .anyMatch(value -> value.name().equals(property));
    }
}
