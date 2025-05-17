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
    private String title;
    private boolean completed;
    private String date; // 문자열 형식 (yyyy-MM-dd)

    public static TodoDto fromEntity(Todo todo) {
        return TodoDto.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .completed(todo.isCompleted())
                .date(todo.getDate().toString())
                .build();
    }

    public Todo toEntity() {
        return Todo.builder()
                .id(this.id)
                .title(this.title)
                .completed(this.completed)
                .date(LocalDate.parse(this.date))
                .build();
    }
}
