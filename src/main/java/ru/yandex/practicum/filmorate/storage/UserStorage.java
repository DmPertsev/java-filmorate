package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User create(User user);

    User update(User user);

    User findById(int id);

    User deleteById(int id);

    List<User> findAll();

    boolean isNotExist (int id);
}