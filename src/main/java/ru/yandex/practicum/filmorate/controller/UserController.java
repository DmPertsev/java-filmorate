package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private int id = 1;
    final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        throwIfUserPrintWrongInfo(user);
        throwIfUserAlreadyExist(user);
        user.setId(id++);
        users.put(user.getId(), user);
        log.info("Пользователь добавлен, Логин: {}, email: {}", user.getLogin(), user.getEmail());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        throwIfUserPrintWrongInfo(user);
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Невозможно обновить данные, так как пользователя не существует");
        }
        throwIfUserAlreadyExist(user);
        users.put(user.getId(), user);
        log.info("Данные пользователя с id: {}, логином: {} успешно обновлена", user.getId(), user.getLogin());
        return user;
    }

    @GetMapping
    public List<User> findAll() {
        return (List<User>) users.values();
    }

    void throwIfUserPrintWrongInfo(@Valid @RequestBody User user) {
        if (user.getLogin().contains(" ")) {
            log.warn("Логин: {}", user.getLogin());
            throw new ValidationException("Логин пользователя не может содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения: {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем!");
        }
    }

    private void throwIfUserAlreadyExist(@RequestBody User userToAdd) {
        boolean exists = users.values().stream()
                .anyMatch(user -> isAlreadyExist(userToAdd, user));
        if (exists) {
            log.warn("Email пользователя: {}", userToAdd);
            throw new ValidationException("Пользователь с таким Email или логином уже существует");
        }
    }

    private boolean isAlreadyExist(User userToAdd, User user) {
        return userToAdd.getLogin().equals(user.getLogin()) ||
                userToAdd.getEmail().equals(user.getEmail());

    }
}