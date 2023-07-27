package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    Film findById(int id);

    Film deleteById(int id);

    List<Film> findAll();

    boolean isNotExist (int id);
}