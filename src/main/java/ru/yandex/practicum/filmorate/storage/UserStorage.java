package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserStorage {

    Optional<User> create(User user);
    Optional<User> update(User user);

    Optional <User> findById(Integer id);
    Optional<User> deleteById(Integer id);
    User findUser(final Integer id);

    Map<Integer, User> findUsers();
    List<User> findAll();

    boolean deleteUser(User user);
    boolean addFriendship(Integer firstId, Integer secondId);
    boolean removeFriendship(Integer userId, Integer friendId);

    boolean isNotExist (int id);
}