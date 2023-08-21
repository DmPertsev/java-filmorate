package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean deleteFilmGenres(int filmId) {
        String sqlQuery = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId);

        return true;
    }

    /*
    @Override
    public void load(List<Film> films) {
        String toSql = String.join(",", Collections.nCopies(films.size(), "?"));
        films.forEach(film -> film.getGenres().clear());
        final Map<Long, Film> filmMap = new HashMap<>();
        for (Film film1 : films) {
            if (filmMap.put((long) film1.getId(), film1) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }
        final String sql = "SELECT * FROM GENRES, FILM_GENRE " +
                "WHERE FILM_GENRE.GENRE_ID = GENRES.GENRE_ID AND FILM_ID IN(" + toSql + ") ";
        jdbcTemplate.query(sql, (rs) -> {
            final Film film = filmMap.get(rs.getLong("FILM_ID"));
            film.getGenres().add(makeGenre(rs, 0));
        }, films.stream().map(Film::getId).toArray());
    }

     */

    @Override
    public boolean addFilmGenres(int filmId, Collection<Genre> genres) {
        for (Genre genre : genres) {
            String sqlQuery = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?) ON CONFLICT DO NOTHING";
            jdbcTemplate.update(sqlQuery, filmId, genre.getId());
        }

        return true;
    }

    @Override
    public List<Genre> getGenresByFilmId(int filmId) {
        String sqlQuery = "SELECT GENRES.GENRE_ID, GENRES.GENRE_NAME FROM GENRES " +
                "INNER JOIN FILM_GENRE ON GENRES.GENRE_ID = FILM_GENRE.GENRE_ID " +
                "WHERE FILM_ID = ?";

        return jdbcTemplate.query(sqlQuery, this::makeGenre, filmId);
    }

    private Genre makeGenre(ResultSet resultSet, int rowNum) throws SQLException {

        return new Genre(resultSet.getInt("GENRE_ID"), resultSet.getString("GENRE_NAME"));
    }

    @Override
    public List<Genre> findAll() {
        String sqlQuery = "SELECT GENRE_ID, GENRE_NAME FROM GENRES ORDER BY GENRE_ID";

        return jdbcTemplate.query(sqlQuery, this::makeGenre);
    }

    @Override
    public Genre findById(int genreId) {
        String sqlQuery = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
        Genre genre;
        try {
            genre = jdbcTemplate.queryForObject(sqlQuery, this::makeGenre, genreId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Жанр с id: '%d' не найден", genreId));
        }

        return genre;
    }
}