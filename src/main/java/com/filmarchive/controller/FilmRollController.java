package com.filmarchive.controller;

import com.filmarchive.dto.FilmRollRequest;
import com.filmarchive.model.FilmRoll;
import com.filmarchive.model.Photo;
import com.filmarchive.service.FilmRollService;
import com.filmarchive.service.PhotoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/film-rolls")
public class FilmRollController {

    private final FilmRollService filmRollService;
    private final PhotoService photoService;

    public FilmRollController(FilmRollService filmRollService, PhotoService photoService) {
        this.filmRollService = filmRollService;
        this.photoService = photoService;
    }

    @GetMapping
    public List<FilmRoll> findAll() {
        return filmRollService.findAll();
    }

    @GetMapping("/{id}")
    public FilmRoll findById(@PathVariable Long id) {
        return filmRollService.findById(id);
    }

    @GetMapping("/{id}/photos")
    public Page<Photo> getPhotos(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return photoService.findByFilmRollId(id, PageRequest.of(page, size));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmRoll create(@Valid @RequestBody FilmRollRequest request) {
        return filmRollService.create(request);
    }

    @PutMapping("/{id}")
    public FilmRoll update(@PathVariable Long id, @Valid @RequestBody FilmRollRequest request) {
        return filmRollService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        filmRollService.delete(id);
    }

    @GetMapping("/photo-counts")
    public Map<Long, Long> photoCounts() {
        return filmRollService.photoCountByRoll();
    }
}
