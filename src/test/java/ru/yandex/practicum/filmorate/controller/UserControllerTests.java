package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {
    @Autowired
    private UserController userController;

    User user = new User();

    @Test
    void createUserEmailTest() {
        user.setEmail("");
        user.setLogin("Login");
        user.setName("Name");
        user.setBirthday(LocalDate.now().minusYears(16));

        RuntimeException trow = assertThrows(RuntimeException.class, () -> {
            userController.create(user);
        });
        assertEquals("HTTP ERROR 400: Email не может быть пустым", trow.getMessage());
    }

    @Test
    void createUserEmptyLoginTest() {
        user.setEmail("user@yandex.ru");
        user.setLogin("");
        user.setName("Name");
        user.setBirthday(LocalDate.now().minusYears(16));
        RuntimeException trow = assertThrows(RuntimeException.class, () -> {
            userController.create(user);
        });
        assertEquals("HTTP ERROR 400: Логин не может быть пустым", trow.getMessage());
    }

    @Test
    void createUserEmptyNameReplaceLoginTest() {
        user.setEmail("user@yandex.ru");
        user.setLogin("Login");
        user.setName("");
        user.setBirthday(LocalDate.now().minusYears(16));
        userController.create(user);
        assertEquals(user.getName(), userController.findById(1).getName());
    }

    @Test
    void createUserFailBirthdayTest() {
        user.setEmail("user@yandex.ru");
        user.setLogin("Login");
        user.setName("Name");
        user.setBirthday(LocalDate.now().plusDays(1));

        RuntimeException trow = assertThrows(RuntimeException.class, () -> {
            userController.create(user);
        });
        assertEquals("HTTP ERROR 400: Дата рождения не может быть в будущем", trow.getMessage());
    }
}