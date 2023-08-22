package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;


import java.sql.*;
import java.sql.Date;
import java.util.*;

@Component("UserDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) {
        final String sqlQuery = "INSERT INTO USERS (EMAIL, LOGIN, USER_NAME, BIRTHDAY) " +
                "VALUES ( ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            final PreparedStatement prepareStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            prepareStatement.setString(1, user.getEmail());
            prepareStatement.setString(2, user.getLogin());
            prepareStatement.setString(3, user.getName());
            prepareStatement.setDate(4, Date.valueOf(user.getBirthday()));
            return prepareStatement;
        }, keyHolder);

        int id = Objects.requireNonNull(keyHolder.getKey()).intValue();

        if (user.getFriends() != null) {
            for (Integer friendId : user.getFriends()) {
                addFriendship(user.getId(), friendId);
            }
        }

        return findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Ошибка при создании пользователя с id=%d", id)));
    }

    @Override
    public User update(User user) {
        final String sqlQuery = "UPDATE USERS SET EMAIL = ?, LOGIN = ?, USER_NAME = ?, BIRTHDAY = ? " +
                "WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(),
                user.getName(), user.getBirthday(), user.getId());

        return findById(user.getId()).orElseThrow(() ->
                new NotFoundException(String.format("Ошибка при обновлении пользователя с id=%d", user.getId())));
    }

    @Override
    public Optional<User> findById(Integer id) {
        final String sqlQuery = "SELECT * FROM USERS WHERE USER_ID = ?";
        User user;
        try {
            user = jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeUser(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("HTTP ERROR 404: Пользователь с id: '" +
                    id + "' не зарегистрирован!");
        }

        return Optional.ofNullable(user);
    }

    @Override
    public User deleteById(Integer id) {
        final String sqlQuery = "DELETE FROM USERS WHERE USER_ID = ?";
        User user = findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Ошибка при удалении пользователя с id=%d", id)));
        jdbcTemplate.update(sqlQuery, id);

        return user;
    }

    @Override
    public boolean deleteUser(User user) {
        String sqlQuery = "DELETE FROM USERS WHERE USER_ID = ?";

        return jdbcTemplate.update(sqlQuery, user.getId()) > 0;
    }

    @Override
    public List<User> findAll() {
        String sqlQuery = "SELECT * FROM USERS";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public boolean addFriendship(Integer userId, Integer friendId) {
        boolean friendAccepted;
        String sqlGetReversFriend = "SELECT * FROM FRIENDSHIP " +
                "WHERE USER_ID = ? AND FRIEND_ID = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlGetReversFriend, friendId, userId);
        friendAccepted = rs.next();
        String sqlSetFriend = "INSERT INTO FRIENDSHIP (USER_ID, FRIEND_ID, STATUS) " +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlSetFriend, userId, friendId, friendAccepted);
        if (friendAccepted) {
            String sqlSetStatus = "UPDATE FRIENDSHIP SET STATUS = true " +
                    "WHERE USER_ID = ? AND FRIEND_ID = ?";
            jdbcTemplate.update(sqlSetStatus, friendId, userId);
        }

        return friendAccepted;
    }

    @Override
    public boolean removeFriendship(Integer userId, Integer friendId) {
        String sqlQuery = "DELETE FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        String sqlSetStatus = "UPDATE FRIENDSHIP SET STATUS = false " +
                "WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sqlSetStatus, friendId, userId);

        return false;
    }

    @Override
    public boolean isNotExist(int id) {

        return false;
    }

    @Override
    public User findUser(Integer id) {
        String sqlQuery = "SELECT * FROM USERS WHERE USER_ID = ?";
        User user;
        try {
            user = jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeUser(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с id: " +
                    id + " не зарегистрирован!");
        }

        return user;
    }

    @Override
    public Map<Integer, User> findUsers() {
        Map<Integer, User> users = new HashMap<>();

        return users;
    }

    private User makeUser(ResultSet resultSet) throws SQLException {
        int userId = resultSet.getInt("USER_ID");

        return new User(
                userId,
                resultSet.getString("EMAIL"),
                resultSet.getString("LOGIN"),
                resultSet.getString("USER_NAME"),
                Objects.requireNonNull(resultSet.getDate("BIRTHDAY")).toLocalDate(),
                getUserFriends(userId));
    }

    private List<Integer> getUserFriends(int userId) {
        String sqlQuery = "SELECT FRIEND_ID FROM FRIENDSHIP WHERE USER_ID = ?";

        return jdbcTemplate.queryForList(sqlQuery, Integer.class, userId);
    }
}