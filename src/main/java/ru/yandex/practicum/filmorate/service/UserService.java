package ru.yandex.practicum.filmorate.service;

import com.sun.jdi.InternalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.ConflictException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
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
        throwIfUserPrintWrongInfo(user);
        if (userStorage.isNotExist(user.getId())) {
            throw new NotFoundException("HTTP ERROR 404: Невозможно обновить данные, так как пользователя не существует");
        }

        return userStorage.update(user);
    }

    public List<User> getAll() {
        log.info("Список пользователей отправлен");

        return userStorage.findAll();
    }

    public User getById(int id) {
        userStorage.isNotExist(id);
        log.info("Пользователь с id: '{}' отправлен", id);

        return userStorage.findById(id);
    }

    public User deleteById(int id) {
        userStorage.isNotExist(id);
        log.info("Пользователь с id: '{}' удален", id);

        return userStorage.deleteById(id);
    }

    public List<User> addFriendship(int firstId, int secondId) {
        userStorage.isNotExist(firstId);
        userStorage.isNotExist(secondId);

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
        userStorage.isNotExist(firstId);
        userStorage.isNotExist(secondId);

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
        userStorage.isNotExist(id);
        log.info("Успех! Запрос получения списка друзей пользователя '{}' выполнен успешно :)",
                userStorage.findById(id).getName());

        return userStorage.findById(id).getFriends().stream()
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriendsList(int firstId, int secondId) {
        userStorage.isNotExist(firstId);
        userStorage.isNotExist(secondId);
        User firstUser = userStorage.findById(firstId);
        User secondUser = userStorage.findById(secondId);
        log.info("Список общих друзей пользователей: '{}' и '{}' успешено отправлен",
                firstUser.getName(), secondUser.getName());

        return firstUser.getFriends().stream()
                .filter(friendId -> secondUser.getFriends().contains(friendId))
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }

    public void throwIfUserPrintWrongInfo(User user) {

        if (user.getLogin().contains(" ") || user.getLogin().isBlank()) {
            log.warn("Введенный Логин пользователя: '{}'", user.getLogin());
            throw new BadRequestException("HTTP ERROR 400: Логин не может быть пустым");
        }

        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
            log.warn("Не заполнено Имя пользователя заменено на Логин: '{}'", user.getName());
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Указанная Дата рождения: '{}'", user.getBirthday());
            throw new BadRequestException("HTTP ERROR 400: Дата рождения не может быть в будущем");
        }

        if (user.getEmail().isBlank() || user.getEmail() == null || user.getEmail().equals(" ")) {
            log.warn("Введенный Email пользователя: '{}'", user.getEmail());
            throw new BadRequestException("HTTP ERROR 400: Email не может быть пустым");
        }
    }

    public void throwIfAlreadyExist(User userToAdd) {
        boolean exists = userStorage.findAll().stream()
                .anyMatch(user -> isAlreadyExist(userToAdd, user));
        if (exists) {
            log.warn("Введенный Email пользователя: '{}'", userToAdd.getEmail());
            throw new ConflictException("HTTP ERROR 409: Пользователь с таким Email или логином уже существует");
        }
    }

    private boolean isAlreadyExist(User userToAdd, User user) {
        return userToAdd.getLogin().equals(user.getLogin()) ||
                userToAdd.getEmail().equals(user.getEmail());
    }
}