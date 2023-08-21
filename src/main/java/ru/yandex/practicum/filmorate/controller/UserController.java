package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public Optional<User> create(@RequestBody User user) {
        log.info("Новый пользователь id: {}", user.getId());

        return userService.create(user);
    }

    @PutMapping
    public Optional<User> update(@Valid @RequestBody User user) {
        userService.setUserNameByLogin(user, "Обновлен");
        log.info("Пользователь id: {} обновлен", user.getId());

        return userService.update(user);
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("GET запрос по адресу: /users");

        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable String id) {
        log.info("GET запрос по адресу: /users/{}", id);
        log.info("Пользователь id: '{}'", id);

        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public Optional<User> deleteById(@PathVariable Integer id) {
        return userService.deleteById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriendship(@PathVariable String id, @PathVariable String friendId) {
        userService.addFriendship(id, friendId);
        log.info("Пользователь id: {} обновлён, добавлен друг id: {}", id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriendship(@PathVariable String id, @PathVariable String friendId) {
        userService.removeFriendship(id, friendId);
        log.info("Пользователь id: {} обновлён, удален друг id: {}", id, friendId);
    }


    @GetMapping("/{id}/friends")
    public Collection<User> findFriends(@PathVariable Integer id) {
        log.info("GET запрос по адресу: /users/{}/friends", id);

        return userService.getUserFriends(String.valueOf(id));
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriendsList(@PathVariable String id, @PathVariable String otherId) {
        log.info("GET запрос по адресу: '/users/{}/friends/common/{}'", id, otherId);

        return userService.getCommonFriendsList(id, otherId);
    }
}