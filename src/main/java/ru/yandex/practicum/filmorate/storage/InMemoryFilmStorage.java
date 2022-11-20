package ru.yandex.practicum.filmorate.storage;

import com.sun.jdi.InternalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.BadRequestException;
import ru.yandex.practicum.filmorate.exceptions.ConflictException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private int id = 1;
    private static final LocalDate DATE_BEFORE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film createNewFilm(Film film) {
        throwIfReleaseDateNotValid(film);
        throwIfAlreadyExist(film);
        film.setId(id++);
        films.put(film.getId(), film);
        log.info("Фильм: {} успешно добавлен в коллекцию", film.getName());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        throwIfReleaseDateNotValid(film);
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("HTTP ERROR 404: Невозможно обновить данные о фильме, так как такого фильма у нас нет");
        }
        throwIfAlreadyExist(film);
        films.put(film.getId(), film);
        log.info("Информация о фильме: {} успешно обновлена", film.getName());
        return film;
    }

    @Override
    public Collection<Film> findAllFilms() {
        return films.values();
    }

    @Override
    public Film getFilmById(int id) {
        return films.get(id);
    }

    @Override
    public Film deleteFilmById(int id) {
        Film film = films.get(id);
        films.remove(id);
        return film;
    }

    @Override
    public Map<Integer, Film> getAllFilms() {
        return films;
    }

    void throwIfReleaseDateNotValid(Film film) {
        if (film.getName().isBlank()) {
            log.warn("Дата выпуска фильма: {}", film.getReleaseDate());
            throw new BadRequestException("HTTP ERROR 400: Название фильма не может быть пустым");
        }
        if (film.getReleaseDate().isBefore(DATE_BEFORE)) {
            log.warn("Дата выпуска фильма: {}", film.getReleaseDate());
            throw new BadRequestException("HTTP ERROR 400: До 28 декабря 1895 года кино не производили");
        }
        if (film.getDuration() < 0) {
            log.warn("Продолжительность фильма: {}", film.getDuration());
            throw new InternalException("HTTP ERROR 500: Продолжительность фильма не может быть меньше нуля");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Текущее описание фильма: {}", film.getDescription());
            throw new BadRequestException("HTTP ERROR 400: Описание должно быть не более 200 символов");
        }
    }

    private void throwIfAlreadyExist(Film filmToAdd) {
        boolean exists = films.values().stream()
                .anyMatch(film -> isAlreadyExist(filmToAdd, film));
        if (exists) {
            log.warn("Фильм к добавлению: {}", filmToAdd);
            throw new ConflictException("HTTP ERROR 409: Такой фильм уже существует в коллекции");
        }
    }

    private boolean isAlreadyExist(Film filmToAdd, Film film) {
        return filmToAdd.getName().equals(film.getName()) &&
                filmToAdd.getReleaseDate().equals(film.getReleaseDate());
    }
}