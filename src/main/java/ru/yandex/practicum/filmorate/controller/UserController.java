package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public User createNewUser(@Valid @RequestBody User user) {
        return userService.createNewUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping
    public Collection<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public User deleteUserById(@PathVariable int id) {
        return userService.deleteUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public List<User> addNewFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.addNewFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public List<User> deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("{id}/friends")
    public List<User> getFriendsListById(@PathVariable int id) {
        return userService.getFriendsListById(id);
    }

    @GetMapping("/{firstId}/friends/common/{secondId}")
    public List<User> getCommonFriendsList(@PathVariable int firstId, @PathVariable int secondId) {
        return userService.getCommonFriendsList(firstId, secondId);
    }
}