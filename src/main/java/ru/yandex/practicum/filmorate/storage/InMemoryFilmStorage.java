package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InternalException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private int id = 1;
    private static final LocalDate DATE_BEFORE = LocalDate.of(1895, 12, 28);
    private static final Map<Integer, Film> films = new HashMap<>();


    @Override
    public Film create(Film film) {
        film.setId(id++);
        films.put(film.getId(), film);
        log.info("Фильм: {} успешно добавлен в коллекцию", film.getName());
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        log.info("Информация о фильме: {} успешно обновлена", film.getName());
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        Collection<Film> allFilms = films.values();
        if (allFilms.isEmpty()) {
            allFilms.addAll(films.values());
        }
        return allFilms;
    }

    @Override
    public Film findById(int id) {
        return films.get(id);
    }

    @Override
    public boolean deleteFilm(Film film) {
        films.remove(film.getId());
        return true;
    }

    @Override
    public List<Film> findAll() {
        return (List<Film>) films;
    }

    @Override
    public boolean addLike(int filmId, int userId) {
        Film film = films.get(filmId);
        film.addLike(userId);
        update(film);
        return true;
    }

    @Override
    public boolean removeLike(int filmId, int userId) {
        Film film = films.get(filmId);
        film.removeLike(userId);
        update(film);
        return true;
    }

    @Override
    public List<Film> getPopularFilms(Integer size) {
        return getAllFilms()
                .stream()
                .filter(film -> film.getLikes() != null)
                .sorted((t1, t2) -> t2.getLikes().size() - t1.getLikes().size())
                .limit(size)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isNotExist(int id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("HTTP ERROR 404: Фильм не найден");
        }
        return false;
    }

    public static void throwIfAlreadyExist(Film filmToAdd) {
        boolean exists = films.values().stream()
                .anyMatch(film -> isAlreadyExist(filmToAdd, film));
        if (exists) {
            log.warn("Фильм к добавлению: {}", filmToAdd);
            throw new ValidationException("Такой фильм уже существует в коллекции");
        }
    }

    private static boolean isAlreadyExist(Film filmToAdd, Film film) {
        return filmToAdd.getName().equals(film.getName()) &&
                filmToAdd.getReleaseDate().equals(film.getReleaseDate());
    }
}