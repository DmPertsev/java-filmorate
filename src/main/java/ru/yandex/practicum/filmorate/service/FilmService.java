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

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class FilmService {

    private final Validator validator;
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final UserService userService;

    @Autowired
    public FilmService(Validator validator, @Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       GenreStorage genreStorage, @Autowired(required = false) UserService userService) {
        this.validator = validator;
        this.filmStorage = filmStorage;
        this.genreStorage = genreStorage;
        this.userService = userService;
    }

    public Film create(Film film) {
        validate(film);

        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validate(film);

        if (filmStorage.isNotExist(film.getId())) {
            throw new NotFoundException("HTTP ERROR 404: Невозможно обновить данные о фильме, так как такого фильма у нас нет");
        }

        return filmStorage.update(film);
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

    private void validate(Film film) {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (!violations.isEmpty()) {
            StringBuilder messageBuilder = new StringBuilder();
            for (ConstraintViolation<Film> filmConstraintViolation : violations) {
                messageBuilder.append(filmConstraintViolation.getMessage());
            }
            throw new BadRequestException("Ошибка валидации Фильма: " + messageBuilder);
        }
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