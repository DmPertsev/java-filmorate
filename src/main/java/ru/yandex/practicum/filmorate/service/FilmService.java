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
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class FilmService {
    private static int counter = 1;
    private final Validator validator;
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final UserService userService;
    private static final LocalDate START_DATA = LocalDate.of(1895, 12, 28);

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
        throwIfReleaseDateNotValid(film);
        validateReleaseDate(film, "");
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        throwIfReleaseDateNotValid(film);
        validate(film);
        if (filmStorage.isNotExist(film.getId())) {
            throw new NotFoundException("HTTP ERROR 404: Невозможно обновить данные о фильме, так как такого фильма у нас нет");
        }
        validateReleaseDate(film, "");
        return filmStorage.update(film);
    }

    // ВРоде все сделал, но не смог побороть 2 теста в Постмане.
    // Пристылаю такой вариант с законменченным вариантом по на N1

    public List<Film> getAll() {
        final List<Film> films = filmStorage.findAll();
        //genreStorage.load(films);
        return films;
    }

    public void addLike(String filmId, String userId) {
        Film film = getFilmStored(filmId);
        User user = userService.getUserById(userId);
        filmStorage.addLike(film.getId(), user.getId());
        log.info("Фильм с id: '{}' получил лайк", filmId);
    }

    public void removeLike(String filmId, String userId) {
        Film film = getFilmStored(filmId);
        User user = userService.getUserById(userId);
        filmStorage.removeLike(film.getId(), user.getId());
        log.info("У Фильма id: '{}' удалён лайк", filmId);
    }

    public Collection<Film> getPopularFilms(String count) {
        Integer size = parseId(count);
        if (size == Integer.MIN_VALUE) {
            size = 10;
        }
        log.info("Список популярных фильмов отправлен");
        return filmStorage.findPopularFilms(size);
    }

    public void validateReleaseDate(Film film, String text) {
        if (film.getReleaseDate().isBefore(START_DATA)) {
            throw new ValidationException("Дата релиза не может быть раньше: " + START_DATA);
        }
        log.debug("{} фильм: '{}'", text, film.getName());
    }

    void throwIfReleaseDateNotValid(Film film) {
        if (film.getName().isBlank()) {
            log.warn("Дата выпуска фильма: {}", film.getReleaseDate());
            throw new BadRequestException("HTTP ERROR 400: Название фильма не может быть пустым");
        }
        if (film.getDuration() < 0) {
            log.warn("Продолжительность фильма: {}", film.getDuration());
            throw new InternalException("HTTP ERROR 500: Продолжительность фильма не может быть меньше нуля");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Текущее описание фильма: {}", film.getDescription());
            throw new  BadRequestException("HTTP ERROR 400: Описание должно быть не более 200 символов");
        }
    }

    private void validate(Film film) {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (!violations.isEmpty()) {
            StringBuilder messageBuilder = new StringBuilder();
            for (ConstraintViolation<Film> filmConstraintViolation : violations) {
                messageBuilder.append(filmConstraintViolation.getMessage());
            }
            throw new ValidationException("Ошибка валидации Фильма: " + messageBuilder);
        }
        if (film.getId() == 0) {
            film.setId(getNextId());
        }
    }

    private static int getNextId() {
        return counter++;
    }

    public Film findById(String id) {
        log.info("Фильм id: '{}' отправлен", id);
        return getFilmStored(id);
    }

    private Integer parseId(final String supposedInt) {
        try {
            return Integer.valueOf(supposedInt);
        } catch (NumberFormatException exception) {
            return Integer.MIN_VALUE;
        }
    }

    private Film getFilmStored(final String supposedId) {
        final int filmId = parseId(supposedId);
        if (filmId == Integer.MIN_VALUE) {
            throw new NotFoundException("HTTP ERROR 404: Не удалось найти id фильма: '{}'", supposedId);
        }
        Film film = filmStorage.findById(filmId);
        if (film == null) {
            throw new NotFoundException(String.format("HTTP ERROR 404: Фильм с id: '%d' не найден", filmId));
        }
        return film;
    }
}