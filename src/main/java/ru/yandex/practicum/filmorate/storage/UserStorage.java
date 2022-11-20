package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {

    User create(User user);

    User update(User user);
    User getById(int id);

    User deleteById(int id);

    List<User> findAll();
    Map<Integer, User> getAll();

}