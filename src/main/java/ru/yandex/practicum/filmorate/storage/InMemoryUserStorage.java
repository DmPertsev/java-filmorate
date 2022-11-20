package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.BadRequestException;
import ru.yandex.practicum.filmorate.exceptions.ConflictException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private int id = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User createNewUser(User user) {
        throwIfUserPrintWrongInfo(user);
        throwIfUserAlreadyExist(user);
        user.setId(id++);
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен с логином: {}, email: {}", user.getLogin(), user.getEmail());
        return user;
    }

    @Override
    public User updateUser(User user) {
        throwIfUserPrintWrongInfo(user);
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("HTTP ERROR 404: Невозможно обновить данные, так как пользователя не существует");
        }
        throwIfUserAlreadyExist(user);
        users.put(user.getId(), user);
        log.info("Данные пользователя с id: {}, логином: {} успешно обновлена", user.getId(), user.getLogin());
        return user;
    }

    @Override
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @Override
    public Map<Integer, User> getUsers() {
        return users;
    }

    @Override
    public User getUserById(int id) {
        return users.get(id);
    }

    @Override
    public User deleteUserById(int id) {
        User user = users.get(id);
        users.remove(id);
        return user;
    }

    void throwIfUserPrintWrongInfo(User user) {
        if (user.getLogin().contains(" ") || user.getLogin().isBlank()) {
            log.warn("Логин: {}", user.getLogin());
            throw new BadRequestException("Логин пользователя не может содержать пробелы");
        }

        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
            log.warn("Не заполнено Имя пользователя заменено на Логин: '{}'", user.getName());
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения: {}", user.getBirthday());
            throw new BadRequestException("Дата рождения не может быть в будущем!");
        }

        if (user.getEmail().isBlank() || user.getEmail() == null || user.getEmail().equals(" ")) {
            log.warn("Введенный Email пользователя: '{}'", user.getEmail());
            throw new BadRequestException("Email не может быть пустым");
        }
    }

    private void throwIfUserAlreadyExist(User userToAdd) {
        boolean exists = users.values().stream()
                .anyMatch(user -> isAlreadyExist(userToAdd, user));
        if (exists) {
            log.warn("Email пользователя: {}", userToAdd);
            throw new ConflictException("HTTP ERROR 409: Пользователь с таким Email или логином уже существует");
        }
    }

    private boolean isAlreadyExist(User userToAdd, User user) {
        return userToAdd.getLogin().equals(user.getLogin()) ||
                userToAdd.getEmail().equals(user.getEmail());

    }



}