package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreService {

    private final GenreStorage genreStorage;

    public Collection<Genre> findAll() {
        return genreStorage.findAll();
    }

    public Collection<Genre> getFilmGenres(int filmId) {
        return genreStorage.getGenresByFilmId(filmId);
    }

    public Genre findById(String strId) {
        int id = parseId(strId);

        return genreStorage.findById(id);
    }

    public boolean deleteFilmGenres(int filmId) {
        return genreStorage.deleteFilmGenres(filmId);
    }

    public boolean addFilmGenres(int filmId, Collection<Genre> genres) {
        return genreStorage.addFilmGenres(filmId, genres);
    }

    private Integer parseId(final String strId) {
        try {
            return Integer.valueOf(strId);
        } catch (NumberFormatException exception) {
            return Integer.MIN_VALUE;
        }
    }
}