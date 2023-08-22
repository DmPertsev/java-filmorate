package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final GenreService genreService;
    private final UserService userService;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       GenreService genreService, @Autowired(required = false) UserService userService) {
        this.filmStorage = filmStorage;
        this.genreService = genreService;
        this.userService = userService;
    }

    public Film create(Film film) {
        Film createdFilm = filmStorage.create(film);
        if (createdFilm.getGenres() != null && !createdFilm.getGenres().isEmpty()) {
            genreService.addFilmGenres(createdFilm.getId(), createdFilm.getGenres());
        }

        return createdFilm;
    }

    public Film update(Film film) {
        Film updatedFilm = filmStorage.update(film);
        genreService.deleteFilmGenres(updatedFilm.getId());
        if (updatedFilm.getGenres() != null && !updatedFilm.getGenres().isEmpty()) {
            genreService.addFilmGenres(updatedFilm.getId(), updatedFilm.getGenres());
        }

        return updatedFilm;
    }

    public List<Film> getAll() {
        final List<Film> films = filmStorage.findAll();

        return films;
    }

    public void addLike(Integer filmId, Integer userId) {
        Optional<Film> film = getFilmStored(filmId);
        User user = userService.getUserById(userId.toString());
        filmStorage.addLike(film.get().getId(), user.getId());
        log.info("Фильм с id: '{}' получил лайк", filmId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        Optional<Film> film = getFilmStored(filmId);
        User user = userService.getUserById(userId.toString());
        filmStorage.removeLike(film.get().getId(), user.getId());
        log.info("У Фильма id: '{}' удалён лайк", filmId);
    }

    public Collection<Film> getPopularFilms(Integer count) {
        log.info("Список популярных фильмов отправлен");

        return filmStorage.findPopularFilms(count);
    }

    public Film findById(Integer id) {
        return filmStorage.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("HTTP ERROR 404: Фильм с id: '%d' не найден", id)));
    }

    private Integer parseId(final String supposedInt) {
        try {
            return Integer.valueOf(supposedInt);
        } catch (NumberFormatException exception) {
            return Integer.MIN_VALUE;
        }
    }

    private Optional<Film> getFilmStored(Integer filmId) {
        Optional<Film> film = filmStorage.findById(filmId);
        if (film.isEmpty()) {
            throw new NotFoundException(String.format("HTTP ERROR 404: Фильм с id: '%d' не найден", filmId));
        }
        return film;
    }
}