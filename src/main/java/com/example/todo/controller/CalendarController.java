package com.example.todo.controller;

import com.example.todo.repository.TodoRepository;
import com.example.todo.entity.Todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.*;

@Controller
public class CalendarController {

    @Autowired
    TodoRepository todoRepository;

    @GetMapping("/calendar")
    public String showCalendar(@RequestParam(required = false) Integer year,
                               @RequestParam(required = false) Integer month,
                               Model model) {
        LocalDate today = LocalDate.now();

        if (year == null || month == null) {
            year = today.getYear();
            month = today.getMonthValue();
        }

        LocalDate firstDay = LocalDate.of(year, month, 1);
        int startDayOfWeek = firstDay.getDayOfWeek().getValue(); // 월=1
        int lastDay = firstDay.lengthOfMonth();

        List<Map<String, Object>> calendar = new ArrayList<>();

        // 빈 칸 추가
        for (int i = 1; i < startDayOfWeek; i++) {
            calendar.add(new HashMap<>());
        }

        // 날짜별 todo 포함하여 추가
        for (int day = 1; day <= lastDay; day++) {
            LocalDate d = LocalDate.of(year, month, day);
            Map<String, Object> map = new HashMap<>();
            map.put("day", day);
            map.put("date", d.toString());
            map.put("isToday", d.equals(today));
            List<Todo> todos = todoRepository.findByDate(d);
            map.put("todos", todos);
            calendar.add(map);
        }

        // 마지막 줄 빈 칸 채우기
        while (calendar.size() % 7 != 0) {
            calendar.add(new HashMap<>());
        }

        // 7개씩 끊어서 calendarRows 생성
        List<List<Map<String, Object>>> calendarRows = new ArrayList<>();
        for (int i = 0; i < calendar.size(); i += 7) {
            calendarRows.add(calendar.subList(i, Math.min(i + 7, calendar.size())));
        }

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        List<Todo> todos = todoRepository.findByDateBetween(start, end);

        long monthlyCompleted = todos.stream().filter(Todo::isCompleted).count();
        long monthlyUncompleted = todos.stream().filter(t -> !t.isCompleted()).count();

        model.addAttribute("monthlyCompleted", monthlyCompleted);
        model.addAttribute("monthlyUncompleted", monthlyUncompleted);

        LocalDate prevMonth = firstDay.minusMonths(1);
        LocalDate nextMonth = firstDay.plusMonths(1);

        model.addAttribute("calendarRows", calendarRows);
        model.addAttribute("calendar", calendar);
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("prevYear", prevMonth.getYear());
        model.addAttribute("prevMonth", prevMonth.getMonthValue());
        model.addAttribute("nextYear", nextMonth.getYear());
        model.addAttribute("nextMonth", nextMonth.getMonthValue());

        return "calendar";
    }
}
