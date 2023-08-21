package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;


public interface FilmStorage {

    Optional <Film> create(Film film);
    Optional<Film> update(Film film);
    Optional<Film> findById(int id);

    List<Film> findAll();
    List<Film> findPopularFilms(Integer count);

    boolean delete(Film film);
    boolean addLike(int filmId, int userId);
    boolean removeLike(int filmId, int userId);
    boolean isNotExist (int id);
}