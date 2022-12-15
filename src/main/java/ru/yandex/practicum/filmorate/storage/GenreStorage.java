package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreStorage {
    Collection<Genre> findAll();
    Collection<Genre> getGenresByFilmId(int filmId);
    Genre findById(int genreId);
    boolean addFilmGenres(int filmId, Collection<Genre> genres);
    boolean deleteFilmGenres(int filmId);
}