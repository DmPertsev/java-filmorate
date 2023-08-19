package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class UserStorageTest {

    private final UserDbStorage userStorage;

    private User user1 = new User(1,
            "1@ya.ru",
            "login1",
            "Name1",
            LocalDate.of(1980, 1, 1),
            new ArrayList<>());
    private User user2 = new User(2,
            "2@ya.ru",
            "login2",
            "Name2",
            LocalDate.of(1980, 1, 1),
            new ArrayList<>());
    private User user3 = new User(3,
            "3@ya.ru",
            "login3",
            "Name3",
            LocalDate.of(1980, 1, 1),
            new ArrayList<>());

    @Test
    public void createUserTest() {
        userStorage.create(user1);

        AssertionsForClassTypes.assertThat(user1).extracting("id").isNotNull();
        AssertionsForClassTypes.assertThat(user1).extracting("name").isNotNull();
    }

    @Test
    public void getAllUsersTest() {
        userStorage.create(user2);
        userStorage.create(user3);

        Collection<User> dbUsers = userStorage.findAll();

        assertEquals(2, dbUsers.size());
    }

    @Test
    public void deleteUserTest() {
        Collection<User> beforeDelete = userStorage.findAll();
        userStorage.deleteUser(user1);
        Collection<User> afterDelete = userStorage.findAll();

        assertEquals(beforeDelete.size() - 1, afterDelete.size());
    }
}