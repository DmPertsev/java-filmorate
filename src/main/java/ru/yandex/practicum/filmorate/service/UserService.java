package ru.yandex.practicum.filmorate.service;

import com.sun.jdi.InternalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public List<User> findAll() {
        log.info("Список пользователей отправлен");
        return userStorage.findAll();
    }

    public User getById(int id) {
        if (!userStorage.getAll().containsKey(id)) {
            throw new NotFoundException("HTTP ERROR 404: Пользователь не найден");
        }
        log.info("Пользователь с id: '{}' отправлен", id);
        return userStorage.getById(id);
    }

    public User deleteById(int id) {
        if (!userStorage.getAll().containsKey(id)) {
            throw new NotFoundException("HTTP ERROR 404: Пользователь не найден. Невозможно удалить неизветсного пользователя");
        }
        log.info("Пользователь с id: '{}' удален", id);
        return userStorage.deleteById(id);
    }

    public List<User> addFriendship(int firstId, int secondId) {
        if (!userStorage.getAll().containsKey(firstId) || !userStorage.getAll().containsKey(secondId)) {
            throw new NotFoundException(String.format("Пользователя с id: %d или с id: %d не существует", firstId, secondId));
        }
        if (userStorage.getById(firstId).getFriends().contains(secondId)) {
            throw new InternalException("Пользователи уже и так являются друзьями");
        }
        userStorage.getById(firstId).getFriends().add(secondId);
        userStorage.getById(secondId).getFriends().add(firstId);
        log.info("Пользователи: '{}' и '{}' теперь являются друзьями :)",
                userStorage.getById(firstId).getName(),
                userStorage.getById(secondId).getName());
        return Arrays.asList(userStorage.getById(firstId), userStorage.getById(secondId));
    }

    public List<User> removeFriendship(int firstId, int secondId) {
        if (!userStorage.getAll().containsKey(firstId) || !userStorage.getAll().containsKey(secondId)) {
            throw new NotFoundException(String.format("Пользователя с id: %d или с id: %d не существует", firstId, secondId));
        }
        if (!userStorage.getById(firstId).getFriends().contains(secondId)) {
            throw new InternalException("Пользователи не являются друзьями");
        }
        userStorage.getById(firstId).getFriends().remove(secondId);
        userStorage.getById(secondId).getFriends().remove(firstId);
        log.info("Пользователи: '{}' и '{}' больше не друзья :(",
                userStorage.getById(firstId).getName(),
                userStorage.getById(secondId).getName());
        return Arrays.asList(userStorage.getById(firstId), userStorage.getById(secondId));
    }

    public List<User> getFriendsListById(int id) {
        if (!userStorage.getAll().containsKey(id)) {
            throw new NotFoundException("HTTP ERROR 404: Невозможно получить список друзей пользователя, " +
                    "так как пользователь не найден :(");
        }
        log.info("Успех! Запрос получения списка друзей пользователя '{}' выполнен успешно :)",
                userStorage.getById(id).getName());
        return userStorage.getById(id).getFriends().stream()
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriendsList(int firstId, int secondId) {
        if (!userStorage.getAll().containsKey(firstId) || !userStorage.getAll().containsKey(secondId)) {
            throw new NotFoundException(String.format("Пользователь с id: %d или с id: %d не существует :(", firstId, secondId));
        }
        User firstUser = userStorage.getById(firstId);
        User secondUser = userStorage.getById(secondId);
        log.info("Список общих друзей пользователей: '{}' и '{}' успешено отправлен",
                firstUser.getName(), secondUser.getName());
        return firstUser.getFriends().stream()
                .filter(friendId -> secondUser.getFriends().contains(friendId))
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }
}