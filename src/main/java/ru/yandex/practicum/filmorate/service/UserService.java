package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class UserService {

    private int counter = 1;
    private final Validator validator;
    private final UserStorage userStorage;


    @Autowired
    public UserService(Validator validator, UserStorage userStorage) {
        this.validator = validator;
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        log.info("Список пользователей отправлен");
        return userStorage.findAll();
    }

    public Optional<User> create(User user) {
        throwIfUserPrintWrongInfo(user);
        validate(user);
        log.info("Создан пользователь");

        return userStorage.create(user);
    }

    public Optional<User> update(User user) {
        throwIfUserPrintWrongInfo(user);
        if (userStorage.isNotExist(user.getId())) {
            throw new NotFoundException("HTTP ERROR 404: Невозможно обновить данные о пользователе");
        }
        validate(user);
        log.info("Обновлен пользователь");
        return userStorage.update(user);
    }

    public Optional<User> findById(Integer id) {
        if (!userStorage.findUsers().containsKey(id)) {
            throw new NotFoundException("HTTP ERROR 404: Пользователь не найден");
        }
        log.info("Пользователь с id: '{}' отправлен", id);
        return userStorage.findById(id);
    }

    public Optional<User> deleteById(int id) {
        if (!userStorage.findUsers().containsKey(id)) {
            throw new NotFoundException("HTTP ERROR 404: Пользователь не найден. Невозможно удалить неизветсного пользователя");
        }
        log.info("Пользователь с id: '{}' удален", id);
        return userStorage.deleteById(id);
    }

    public void addFriendship(final String supposedUserId, final String supposedFriendId) {
        User user = getUserStored(supposedUserId);
        User friend = getUserStored(supposedFriendId);
        userStorage.addFriendship(user.getId(), friend.getId());
        log.info("Пользователь с id: '{}' добавлен с список друзей пользователя с id: '{}'", supposedUserId, supposedFriendId);
    }

    public void removeFriendship(final String supposedUserId, final String supposedFriendId) {
        User user = getUserStored(supposedUserId);
        User friend = getUserStored(supposedFriendId);
        userStorage.removeFriendship(user.getId(), friend.getId());
        log.info("Пользователь с id: '{}' добавлен с список друзей пользователя с id: '{}'", supposedUserId, supposedFriendId);
    }

    public Collection<User> getUserFriends(String userId) {
        User user = getUserStored(userId);
        Collection<User> friends = new HashSet<>();
        for (Integer id : user.getFriends()) {
            friends.add(userStorage.findUser(id));
        }
        return friends;
    }

    public User getUserById(final String supposedId) {
        return getUserStored(supposedId);
    }

    private User getUserStored(final String supposedId) {
        final int userId = parseId(supposedId);
        if (userId == Integer.MIN_VALUE) {
            throw new NotFoundException(String.format("Не удалось найти id пользователя: %d", supposedId));
        }
        User user = userStorage.findUser(userId);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id: '%d' не зарегистрирован!", userId));
        }
        return user;
    }

    private Integer parseId(final String id) {
        try {
            return Integer.valueOf(id);
        } catch (NumberFormatException exception) {
            return Integer.MIN_VALUE;
        }
    }

    public void setUserNameByLogin(User user, String text) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        log.debug("{} пользователь: '{}', email: '{}'", text, user.getName(), user.getEmail());
    }

    void throwIfUserPrintWrongInfo(User user) {
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
            throw new BadRequestException("Дата рождения не может быть в будущем");
        }

        if (user.getEmail().isBlank() || user.getEmail() == null || user.getEmail().equals(" ")) {
            log.warn("Введенный Email пользователя: '{}'", user.getEmail());
            throw new BadRequestException("HTTP ERROR 400:Email не может быть пустым");
        }
    }

    private void validate(final User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
            log.info("UserService: Поле name не задано. Установлено значение {} из поля login", user.getLogin());
        } else if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("UserService: Поле name не содержит буквенных символов. " +
                    "Установлено значение {} из поля login", user.getLogin());
        }
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            StringBuilder messageBuilder = new StringBuilder();
            for (ConstraintViolation<User> userConstraintViolation : violations) {
                messageBuilder.append(userConstraintViolation.getMessage());
            }
            throw new BadRequestException("Ошибка валидации Пользователя: " + messageBuilder);
        }
        if (user.getId() == 0) {
            user.setId(counter++);
        }
    }

    public Collection<User> getCommonFriendsList(final String supposedUserId, final String supposedOtherId) {
        User user = getUserStored(supposedUserId);
        User otherUser = getUserStored(supposedOtherId);
        Collection<User> commonFriends = new HashSet<>();
        for (Integer id : user.getFriends()) {
            if (otherUser.getFriends().contains(id)) {
                commonFriends.add(userStorage.findUser(id));
            }
        }
        return commonFriends;
    }
}