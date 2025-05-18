package com.example.todo.controller;

import com.example.todo.entity.Todo;
import com.example.todo.repository.TodoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/todos")
@Slf4j
public class TodoController {

    @Autowired
    TodoRepository todoRepository;

    @GetMapping
    public String listTodos(@RequestParam(name = "date", required = false) String date, Model model) {
        if (date == null || date.isEmpty()) {
            date = LocalDate.now().toString();
        }

        LocalDate parsedDate = LocalDate.parse(date);
        List<Todo> todos = todoRepository.findByDate(parsedDate);

        log.info("date {}", parsedDate);
        for (Todo todo : todos) {
            log.info("Todo - id: {}, title: {}, completed: {}, date: {}",
                    todo.getId(), todo.getTitle(), todo.isCompleted(), todo.getDate());
        }

        String formattedDate = String.format("%d년 %d월 %d일 할 일",
                parsedDate.getYear(), parsedDate.getMonthValue(), parsedDate.getDayOfMonth());

        model.addAttribute("dateTitle", formattedDate);
        model.addAttribute("date", date);
        model.addAttribute("todos", todos);
        return "todos"; // 💡 templates/todos.mustache
    }

    @PostMapping("/add")
    public String addTodo(@RequestParam("title") String title, @RequestParam("date") String date) {
        todoRepository.save(Todo.builder()
                .title(title)
                .completed(false)
                .date(LocalDate.parse(date))
                .build());
        return "redirect:/todos?date=" + date;
    }

    @PostMapping("/toggle/{id}")
    public String toggleTodo(@PathVariable("id") Long id, @RequestParam("date") String date) {
        Todo todo = todoRepository.findById(id).orElseThrow();
        todo.setCompleted(!todo.isCompleted());
        todoRepository.save(todo);
        return "redirect:/todos?date=" + date;
    }

    @PostMapping("/delete/{id}")
    public String deleteTodo(@PathVariable("id") Long id, @RequestParam("date") String date) {
        todoRepository.deleteById(id);
        return "redirect:/todos?date=" + date;
    }

    // ✅ [2] 수정 처리
    @PostMapping("/update/{id}")
    public String updateTodo(@PathVariable("id") Long id,
                             @RequestParam("title") String title,
                             @RequestParam("date") String date) {
        Todo todo = todoRepository.findById(id).orElseThrow();
        todo.setTitle(title);
        todoRepository.save(todo);
        return "redirect:/todos?date=" + date;
    }
}
