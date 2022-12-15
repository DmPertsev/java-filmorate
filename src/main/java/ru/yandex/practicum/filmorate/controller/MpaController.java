package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public Collection<Mpa> findAll() {
        log.info("Получен запрос GET по адресу: '/mpa'");
        return mpaService.findAll();
    }

    @GetMapping("/{id}")
    public Mpa findMpaById(@PathVariable String id) {
        log.info("Получен запрос GET по адресу: '/mpa/{}'", id);
        return mpaService.findMpaById(id);
    }
}