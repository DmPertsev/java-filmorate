package ru.yandex.practicum.filmorate.service;

import com.sun.jdi.InternalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
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

    public User createNewUser(User user) {
        return userStorage.createNewUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public Collection<User> findAllUsers() {
        log.info("Список пользователей отправлен");
        return userStorage.findAllUsers();
    }

    public User getUserById(int id) {
        if (!userStorage.getUsers().containsKey(id)) {
            throw new NotFoundException("HTTP ERROR 404: Пользователь не найден");
        }
        log.info("Пользователь с id: '{}' отправлен", id);
        return userStorage.getUserById(id);
    }

    public User deleteUserById(int id) {
        if (!userStorage.getUsers().containsKey(id)) {
            throw new NotFoundException("HTTP ERROR 404: Пользователь не найден. Невозможно удалить неизветсного пользователя");
        }
        log.info("Пользователь с id: '{}' удален", id);
        return userStorage.deleteUserById(id);
    }

    public List<User> addNewFriend(int firstId, int secondId) {
        if (!userStorage.getUsers().containsKey(firstId) || !userStorage.getUsers().containsKey(secondId)) {
            throw new NotFoundException(String.format("HTTP ERROR 404: Пользователя с id: %d или с id: %d не существует", firstId, secondId));
        }
        if (userStorage.getUserById(firstId).getFriends().contains(secondId)) {
            throw new InternalException("Пользователи уже и так являются друзьями");
        }
        userStorage.getUserById(firstId).getFriends().add(secondId);
        userStorage.getUserById(secondId).getFriends().add(firstId);
        log.info("Пользователи: '{}' и '{}' теперь являются друзьями :)",
                userStorage.getUserById(firstId).getName(),
                userStorage.getUserById(secondId).getName());
        return Arrays.asList(userStorage.getUserById(firstId), userStorage.getUserById(secondId));
    }

    public List<User> deleteFriend(int firstId, int secondId) {
        if (!userStorage.getUsers().containsKey(firstId) || !userStorage.getUsers().containsKey(secondId)) {
            throw new NotFoundException(String.format("HTTP ERROR 404: Пользователя с id: %d или с id: %d не существует", firstId, secondId));
        }
        if (!userStorage.getUserById(firstId).getFriends().contains(secondId)) {
            throw new InternalException("Пользователи не являются друзьями");
        }
        userStorage.getUserById(firstId).getFriends().remove(secondId);
        userStorage.getUserById(secondId).getFriends().remove(firstId);
        log.info("Пользователи: '{}' и '{}' больше не друзья :(",
                userStorage.getUserById(firstId).getName(),
                userStorage.getUserById(secondId).getName());
        return Arrays.asList(userStorage.getUserById(firstId), userStorage.getUserById(secondId));
    }

    public List<User> getFriendsListById(int id) {
        if (!userStorage.getUsers().containsKey(id)) {
            throw new NotFoundException("HTTP ERROR 404: невозможно получить список друзей пользователя, " +
                    "так как пользователь не найден :(");
        }
        log.info("Успех! Запрос получения списка друзей пользователя '{}' выполнен успешно :)",
                userStorage.getUserById(id).getName());
        return userStorage.getUserById(id).getFriends().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriendsList(int firstId, int secondId) {
        if (!userStorage.getUsers().containsKey(firstId) || !userStorage.getUsers().containsKey(secondId)) {
            throw new NotFoundException(String.format("HTTP ERROR 404: Пользователь с id: %d или с id: %d не существует :(", firstId, secondId));
        }
        User firstUser = userStorage.getUserById(firstId);
        User secondUser = userStorage.getUserById(secondId);
        log.info("Список общих друзей пользователей: '{}' и '{}' успешено отправлен",
                firstUser.getName(), secondUser.getName());
        return firstUser.getFriends().stream()
                .filter(friendId -> secondUser.getFriends().contains(friendId))
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }
}