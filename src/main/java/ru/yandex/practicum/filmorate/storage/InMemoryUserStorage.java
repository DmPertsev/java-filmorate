package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private int id = 1;
    private static final Map<Integer, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        user.setId(id++);
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен с логином: {}, email: {}", user.getLogin(), user.getEmail());
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new ObjectNotFoundException("Невозможно обновить данные, так как пользователя не существует");
        }
        users.put(user.getId(), user);
        log.info("Данные пользователя с id: {}, логином: {} успешно обновлена", user.getId(), user.getLogin());
        return user;
    }

    @Override
    public List<User> findAll() {
        return (List<User>) users.values();
    }

    @Override
    public Map<Integer, User> getUsers() {
        return users;
    }

    @Override
    public User findById(Integer id) {
        return users.get(id);
    }

    @Override
    public User deleteById(Integer id) {
        User user = users.get(id);
        users.remove(id);
        return user;
    }

    @Override
    public boolean deleteUser(User user) {
        users.remove(user.getId());
        return true;
    }

    @Override
    public boolean addFriendship(Integer userId, Integer friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);
        user.addFriendship(friendId);
        friend.addFriendship(userId);
        update(user);
        update(friend);
        return true;
    }

    @Override
    public boolean removeFriendship(Integer userId, Integer friendId) {
        return false;
    }

    @Override
    public boolean isNotExist(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("HTTP ERROR 404: Пользователь не найден");
        }
        return false;
    }

    public static void throwIfAlreadyExist(User userToAdd) {
        boolean exists = users.values().stream()
                .anyMatch(user -> isAlreadyExist(userToAdd, user));
        if (exists) {
            log.warn("Введенный Email пользователя: '{}'", userToAdd);
            throw new ValidationException("Пользователь с таким Email или логином уже существует");
        }
    }

    private static boolean isAlreadyExist(User userToAdd, User user) {
        return userToAdd.getLogin().equals(user.getLogin()) ||
                userToAdd.getEmail().equals(user.getEmail());
    }

    @Override
    public User getUser(final Integer id) {
        return users.get(id);
    }
}