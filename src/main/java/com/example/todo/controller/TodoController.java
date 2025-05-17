package com.example.todo.controller;

import com.example.todo.dto.TodoDto;
import com.example.todo.entity.Todo;
import com.example.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class TodoController {

    private final TodoRepository todoRepository;

    @GetMapping("/todos")
    public String getTodos(@RequestParam(required = false) String date,
                           @RequestParam(required = false) Integer year,
                           @RequestParam(required = false) Integer month,
                           Model model) {

        LocalDate today = LocalDate.now();
        LocalDate selectedDate;

        if (date != null) {
            selectedDate = LocalDate.parse(date);
        } else if (year != null && month != null) {
            selectedDate = LocalDate.of(year, month, 1);
        } else {
            selectedDate = today;
        }

        int selectedYear = selectedDate.getYear();
        int selectedMonth = selectedDate.getMonthValue();
        LocalDate firstDay = LocalDate.of(selectedYear, selectedMonth, 1);
        int startDayOfWeek = firstDay.getDayOfWeek().getValue(); // 월(1)~일(7)
        int lastDay = selectedDate.lengthOfMonth();

        List<Map<String, Object>> calendar = new ArrayList<>();

        // 빈 칸 추가 (1~startDayOfWeek-1)
        for (int i = 1; i < startDayOfWeek; i++) {
            calendar.add(new HashMap<>()); // 빈 셀
        }

        // 날짜 셀 추가
        for (int day = 1; day <= lastDay; day++) {
            LocalDate d = LocalDate.of(selectedYear, selectedMonth, day);
            Map<String, Object> map = new HashMap<>();
            map.put("day", day);
            map.put("date", d.toString());
            map.put("isToday", d.equals(today));
            calendar.add(map);
        }

        // 다음/이전 월 계산
        LocalDate prevMonthDate = selectedDate.minusMonths(1);
        LocalDate nextMonthDate = selectedDate.plusMonths(1);

        List<TodoDto> todos = todoRepository.findByDate(selectedDate)
                .stream().map(TodoDto::fromEntity).collect(Collectors.toList());

        model.addAttribute("calendar", calendar);
        model.addAttribute("todos", todos);
        model.addAttribute("selectedDate", selectedDate.toString());
        model.addAttribute("selectedYear", selectedYear);
        model.addAttribute("selectedMonth", selectedMonth);
        model.addAttribute("prevYear", prevMonthDate.getYear());
        model.addAttribute("prevMonth", prevMonthDate.getMonthValue());
        model.addAttribute("nextYear", nextMonthDate.getYear());
        model.addAttribute("nextMonth", nextMonthDate.getMonthValue());

        // 달력 마지막 줄 빈 칸 채우기
        while (calendar.size() % 7 != 0) {
            calendar.add(new HashMap<>()); // 빈 셀
        }

        // 7개씩 끊어서 calendarRows 생성
        List<List<Map<String, Object>>> calendarRows = new ArrayList<>();
        for (int i = 0; i < calendar.size(); i += 7) {
            calendarRows.add(calendar.subList(i, Math.min(i + 7, calendar.size())));
        }

        // ... 기존 model.addAttribute
        model.addAttribute("calendarRows", calendarRows);

        return "todos";
    }

    @PostMapping("/todos/add")
    public String addTodo(@RequestParam String title, @RequestParam String date) {
        Todo newTodo = Todo.builder()
                .title(title)
                .completed(false)
                .date(LocalDate.parse(date))
                .build();
        todoRepository.save(newTodo);
        return "redirect:/todos?date=" + date;
    }

    @PostMapping("/todos/toggle/{id}")
    public String toggleTodo(@PathVariable Long id, @RequestParam String date) {
        Todo todo = todoRepository.findById(id).orElseThrow();
        todo.setCompleted(!todo.isCompleted());
        todoRepository.save(todo);
        return "redirect:/todos?date=" + date;
    }

    @PostMapping("/todos/delete/{id}")
    public String deleteTodo(@PathVariable Long id, @RequestParam String date) {
        todoRepository.deleteById(id);
        return "redirect:/todos?date=" + date;
    }
}
