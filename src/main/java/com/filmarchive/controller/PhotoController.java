package com.filmarchive.controller;

import com.filmarchive.model.Photo;
import com.filmarchive.service.PhotoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping
    public Page<Photo> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return photoService.findAll(PageRequest.of(page, size));
    }

    @PostMapping("/film-rolls/{filmRollId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Photo upload(
            @PathVariable Long filmRollId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String caption,
            @RequestParam(required = false) String location
    ) {
        return photoService.upload(filmRollId, file, caption, location);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        photoService.delete(id);
    }
}
