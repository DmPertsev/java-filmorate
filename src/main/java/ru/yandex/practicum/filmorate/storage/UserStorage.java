package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {

    User create(User user);
    User update(User user);
    User findById(Integer id);
    User deleteById(Integer id);
    User findUser(final Integer id);

    Map<Integer, User> findUsers();
    List<User> findAll();

    boolean deleteUser(User user);
    boolean addFriendship(Integer firstId, Integer secondId);
    boolean removeFriendship(Integer userId, Integer friendId);

    boolean isNotExist (int id);
}