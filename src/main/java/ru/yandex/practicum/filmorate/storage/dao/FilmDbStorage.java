package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component("FilmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreService genreService;
    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);

    @Override
    public Optional<Film> findById(int filmId) {
        String sqlQuery = "SELECT * FROM FILMS " +
                "INNER JOIN RATING_MPA ON FILMS.RATING_ID = RATING_MPA.RATING_ID " +
                "WHERE FILM_ID = ?";
        Film film;
        try {
            film = jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeFilm(rs), filmId);
        } catch (EmptyResultDataAccessException exception) {
            throw new NotFoundException(String.format("HTTP ERROR 404: Фильма с id=%d нет в базе данных", filmId));
        }
        log.info("Найден фильм: {} {}", film.getId(), film.getName());

        return Optional.of(film);
    }

    @Override
    public List<Film> findAll() {
        final String sqlQuery = "SELECT * FROM FILMS, RATING_MPA WHERE FILMS.RATING_ID = RATING_MPA.RATING_ID ";

        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> makeFilm(resultSet));
    }

    @Override
    public Optional<Film> create(Film film) {
        String sqlQuery = "INSERT INTO FILMS " +
                "(FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATE, RATING_ID) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement prepareStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            prepareStatement.setString(1, film.getName());
            prepareStatement.setString(2, film.getDescription());
            prepareStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            prepareStatement.setLong(4, film.getDuration());
            prepareStatement.setInt(5, film.getRate());
            prepareStatement.setInt(6, Math.toIntExact(film.getMpa().getId()));
            return prepareStatement;
        }, keyHolder);
        int id = Objects.requireNonNull(keyHolder.getKey()).intValue();
        film.setId(id);


        if (!film.getGenres().isEmpty()) {
            genreService.addFilmGenres(film.getId(), film.getGenres());
        }
        if (film.getLikes() != null) {
            for (Integer userId : film.getLikes()) {
                addLike(film.getId(), userId);
            }
        }

        return findById(id);
    }

    @Override
    public Optional<Film> update(Film film) {
        String sqlQueryDel = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQueryDel, film.getId());

        String sqlQuery = "UPDATE FILMS " +
                "SET FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATE = ? , RATING_ID = ? " +
                "WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());
        genreService.deleteFilmGenres(film.getId());
        if (!film.getGenres().isEmpty()) {
            genreService.addFilmGenres(film.getId(), film.getGenres());
        }
        if (film.getLikes() != null) {
            for (Integer userId : film.getLikes()) {
                addLike(film.getId(), userId);
            }
        }

        return findById(film.getId());
    }

    @Override
    public boolean delete(Film film) {
        String sqlQuery = "DELETE FROM FILMS WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getId());

        return true;
    }

    @Override
    public boolean addLike(int filmId, int userId) {
        String sqlQuery = "SELECT * FROM LIKES WHERE USER_ID = ? AND FILM_ID = ?";
        SqlRowSet existLike = jdbcTemplate.queryForRowSet(sqlQuery, userId, filmId);
        if (!existLike.next()) {
            String setLike = "INSERT INTO LIKES (USER_ID, FILM_ID) VALUES  (?, ?) ";
            jdbcTemplate.update(setLike, userId, filmId);
        }
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(sqlQuery, userId, filmId);
        log.info(String.valueOf(resultSet.next()));

        return resultSet.next();
    }

    @Override
    public boolean removeLike(int filmId, int userId) {
        String deleteLike = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(deleteLike, filmId, userId);

        return true;
    }

    @Override
    public List<Film> findPopularFilms(Integer count) {
        String sqlQuery = "SELECT COUNT(L.LIKE_ID) AS like_rate, " +
                "FILMS.FILM_ID, FILMS.FILM_NAME, FILMS.DESCRIPTION, " +
                "FILMS.RELEASE_DATE, FILMS.DURATION, FILMS.RATE, R.RATING_ID, R.MPA_NAME, R.DESCRIPTION FROM FILMS " +
                "LEFT JOIN LIKES AS L ON L.FILM_ID = FILMS.FILM_ID " +
                "INNER JOIN RATING_MPA AS R ON R.RATING_ID = FILMS.RATING_ID " +
                "GROUP BY FILMS.FILM_ID " +
                "ORDER BY like_rate DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), count);
    }

    @Override
    public boolean isNotExist(int id) {
        return false;
    }

    // c N+1. Не получилось сделать вариант, чтоб проходил тесты постамана...

    private Film makeFilm(ResultSet resultSet) throws SQLException {
        int filmId = resultSet.getInt("FILM_ID");
        Film film = new Film(
                filmId,
                resultSet.getString("FILMS.FILM_NAME"),
                resultSet.getString("FILMS.DESCRIPTION"),
                Objects.requireNonNull(resultSet.getDate("FILMS.RELEASE_DATE")).toLocalDate(),
                resultSet.getInt("FILMS.DURATION"),
                resultSet.getInt("FILMS.RATE"),
                new Mpa(resultSet.getInt("RATING_MPA.RATING_ID"),
                        resultSet.getString("RATING_MPA.MPA_NAME"),
                        resultSet.getString("RATING_MPA.DESCRIPTION")),
                (List<Genre>) genreService.getFilmGenres(filmId),
                getFilmLikes(filmId)
        );

        return film;
    }

    private List<Integer> getFilmLikes(Integer filmId) {
        String sqlQuery = "SELECT USER_ID FROM LIKES WHERE FILM_ID = ?";

        return jdbcTemplate.queryForList(sqlQuery, Integer.class, filmId);
    }
}