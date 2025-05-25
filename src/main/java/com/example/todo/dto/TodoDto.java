package com.example.todo.dto;

import com.example.todo.entity.Todo;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoDto {
    private Long id;
    private String task;
    private boolean completed;
    private LocalDate date; // 문자열 형식 (yyyy-MM-dd)

    public Todo toEntity() {
        return new Todo(id, task, completed, date);
    }
}