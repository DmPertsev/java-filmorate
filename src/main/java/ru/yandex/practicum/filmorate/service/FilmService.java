package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    public Film createNewFilm(Film film) {
        return filmStorage.createNewFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Collection<Film> findAllFilms() {
        log.info("Список фильмов отправлен");
        return filmStorage.findAllFilms();
    }

    public Film getFilmByID(int id) {
        if (!filmStorage.getAllFilms().containsKey(id)) {
            throw new NotFoundException("HTTP ERROR 404: Фильм не найден");
        }
        log.info("Фильм с id: " + id + " отправлен");
        return filmStorage.getFilmByID(id);
    }

    public Film deleteFilmById(int id) {
        if (!filmStorage.getAllFilms().containsKey(id)) {
            throw new NotFoundException("HTTP ERROR 404: Фильм не найден, удаление невозможно");
        }
        log.info("Фильм с id: " + id + " удалён");
        return filmStorage.deleteFilmById(id);
    }

    public Film addNewLike(int filmId, int userId) {
        if (!filmStorage.getAllFilms().containsKey(filmId)) {
            throw new NotFoundException("HTTP ERROR 404: Фильм не найден");
        }
        filmStorage.getFilmByID(filmId).getLikes().add(userId);
        log.info("Пользователь с id: {} поставил лайк фильму с id {}", userId, filmId);
        return filmStorage.getFilmByID(filmId);
    }

    public Film deleteLike(int filmId, int userId) {
        if (!filmStorage.getAllFilms().containsKey(filmId)) {
            throw new NotFoundException("HTTP ERROR 404: Фильм не найден");
        }
        if (!filmStorage.getFilmByID(filmId).getLikes().contains(userId)) {
            throw new NotFoundException("HTTP ERROR 404: Нет лайка от пользователя");
        }
        filmStorage.getFilmByID(filmId).getLikes().contains(userId);
        log.info("Пользователь с id: {} удалил лайк фильму с id {}", userId, filmId);
        return filmStorage.getFilmByID(filmId);
    }
    public List<Film> getPopularFilms(int count) {
        log.info("Список популярных фильмов отправлен");

        return filmStorage.findAllFilms().stream()
                .sorted((o1, o2) -> Integer.compare(o2.getLikes().size(), o1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}