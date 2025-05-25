package com.example.todo.controller;

import com.example.todo.dto.TodoDto;
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


        String formattedDate = String.format("%d년 %d월 %d일 할 일",
                parsedDate.getYear(), parsedDate.getMonthValue(), parsedDate.getDayOfMonth());

        model.addAttribute("dateTitle", formattedDate);
        model.addAttribute("date", date);
        model.addAttribute("todos", todos);

        return "todos"; // 💡 Mustache 파일 이름: resources/templates/todos.mustache
    }

    @PostMapping("/add")
    public String addTodo(@RequestParam("task") String task, @RequestParam("date") String date) {
        TodoDto todoDto = TodoDto.builder()
                .task(task)
                .completed(false)
                .date(LocalDate.parse(date))
                .build();
        todoRepository.save(todoDto.toEntity());
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
        Todo target = todoRepository.findById(id).orElse(null);
        if( target != null) {
            todoRepository.deleteById(id);
        }
        return "redirect:/todos?date=" + date;

    }

    @PostMapping("/update/{id}")
    public String updateTodo(@PathVariable("id") Long id,
                             @RequestParam("task") String task,
                             @RequestParam("date") String date) {
        Todo todo = todoRepository.findById(id).orElseThrow();
        todo.setTask(task);
        todoRepository.save(todo);
        return "redirect:/todos?date=" + date;
    }

    @PostMapping("/deleteAllMonth")
    public String deleteAllByMonth(@RequestParam("year") int year, @RequestParam("month") int month) {
        // 경고
        log.warn("Deleting all todos for year: {}, month: {}", year, month);

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        List<Todo> todos = todoRepository.findByDateBetween(start, end);
        todoRepository.deleteAll(todos);


        return "redirect:/calendar?year=" + year + "&month=" + month;
    }
}