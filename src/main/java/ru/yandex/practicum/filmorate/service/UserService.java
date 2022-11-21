package ru.yandex.practicum.filmorate.service;

import com.sun.jdi.InternalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Arrays;
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

    public User findById(int id) {
        userStorage.isExist(id);
        log.info("Пользователь с id: '{}' отправлен", id);
        return userStorage.findById(id);
    }

    public User deleteById(int id) {
        userStorage.isExist(id);
        log.info("Пользователь с id: '{}' удален", id);
        return userStorage.deleteById(id);
    }

    public List<User> addFriendship(int firstId, int secondId) {
        userStorage.isExist(firstId);
        userStorage.isExist(secondId);
        if (userStorage.findById(firstId).getFriends().contains(secondId)) {
            throw new InternalException("Пользователи уже и так являются друзьями");
        }
        userStorage.findById(firstId).getFriends().add(secondId);
        userStorage.findById(secondId).getFriends().add(firstId);
        log.info("Пользователи: '{}' и '{}' теперь являются друзьями :)",
                userStorage.findById(firstId).getName(),
                userStorage.findById(secondId).getName());
        return Arrays.asList(userStorage.findById(firstId), userStorage.findById(secondId));
    }

    public List<User> removeFriendship(int firstId, int secondId) {
        userStorage.isExist(firstId);
        userStorage.isExist(secondId);
        if (!userStorage.findById(firstId).getFriends().contains(secondId)) {
            throw new InternalException("Пользователи не являются друзьями");
        }
        userStorage.findById(firstId).getFriends().remove(secondId);
        userStorage.findById(secondId).getFriends().remove(firstId);
        log.info("Пользователи: '{}' и '{}' больше не друзья :(",
                userStorage.findById(firstId).getName(),
                userStorage.findById(secondId).getName());
        return Arrays.asList(userStorage.findById(firstId), userStorage.findById(secondId));
    }

    public List<User> getFriendsListById(int id) {
        userStorage.isExist(id);
        log.info("Успех! Запрос получения списка друзей пользователя '{}' выполнен успешно :)",
                userStorage.findById(id).getName());
        return userStorage.findById(id).getFriends().stream()
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriendsList(int firstId, int secondId) {
        userStorage.isExist(firstId);
        userStorage.isExist(secondId);
        User firstUser = userStorage.findById(firstId);
        User secondUser = userStorage.findById(secondId);
        log.info("Список общих друзей пользователей: '{}' и '{}' успешено отправлен",
                firstUser.getName(), secondUser.getName());
        return firstUser.getFriends().stream()
                .filter(friendId -> secondUser.getFriends().contains(friendId))
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }
}