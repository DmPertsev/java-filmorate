package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    Film getById(int id);
    Film deleteById(int id);

    List<Film> findAll();
    Map<Integer, Film> getAll();
}