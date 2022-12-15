package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserStorage {

    User create(User user);
    User update(User user);
    User findById(Integer id);
    User deleteById(Integer id);
    boolean deleteUser(User user);
    List<User> findAll();
    boolean addFriendship(Integer firstId, Integer secondId);
    boolean removeFriendship(Integer userId, Integer friendId);
    Map<Integer, User> getUsers();
    User getUser(final Integer id);

    boolean isNotExist (int id);
}