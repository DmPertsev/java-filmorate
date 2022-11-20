package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;

public interface FilmStorage {

    Film createNewFilm(Film film);
    Film updateFilm (Film film);
    Film getFilmByID (int id);
    Film deleteFilmById(int id);

    Collection<Film> findAllFilms();
    Map<Integer, Film> getAllFilms();
}