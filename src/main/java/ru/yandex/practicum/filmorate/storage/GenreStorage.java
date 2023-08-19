package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

public interface GenreStorage {

    List<Genre> findAll();
    Collection<Genre> getGenresByFilmId(int filmId);

    Genre findById(int genreId);

    boolean addFilmGenres(int filmId, Collection<Genre> genres);

    boolean deleteFilmGenres(int filmId);
}