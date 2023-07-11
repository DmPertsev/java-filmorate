package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.BadRequestException;
import ru.yandex.practicum.filmorate.exceptions.ConflictException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private int id = 1;
    final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        throwIfUserPrintWrongInfo(user);
        throwIfUserAlreadyExist(user);
        user.setId(id++);
        users.put(user.getId(), user);
        log.info("Пользователь добавлен, Логин: {}, email: {}", user.getLogin(), user.getEmail());

        return user;
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        throwIfUserPrintWrongInfo(user);
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("HTTP ERROR 404: Невозможно обновить данные, так как пользователя не существует");
        }
        throwIfUserAlreadyExist(user);
        users.put(user.getId(), user);
        log.info("Данные пользователя с id: {}, логином: {} успешно обновлена", user.getId(), user.getLogin());

        return user;
    }

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    void throwIfUserPrintWrongInfo(@Valid @RequestBody User user) {
        if (user.getLogin().contains(" ")) {
            log.warn("Логин: {}", user.getLogin());
            throw new BadRequestException("Логин пользователя не может содержать пробелы");
        }
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
            log.warn("Не заполнено Имя пользователя заменено на Логин: '{}'", user.getName());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения: {}", user.getBirthday());
            throw new BadRequestException("Дата рождения не может быть в будущем!");
        }

        String name = user.getName();
        if (name == null || name.isBlank()) {
            name = user.getLogin();
            user.setName(name);
        }
    }

    private void throwIfUserAlreadyExist(@RequestBody User userToAdd) {
        boolean exists = users.values().stream()
                .anyMatch(user -> isAlreadyExist(userToAdd, user));
        if (exists) {
            log.warn("Email пользователя: {}", userToAdd);
            throw new ConflictException("HTTP ERROR 409: Пользователь с таким Email или логином уже существует");
        }
    }

    private boolean isAlreadyExist(User userToAdd, User user) {
        return userToAdd.getLogin().equals(user.getLogin()) ||
                userToAdd.getEmail().equals(user.getEmail());

    }
}