package com.filmarchive.service;

import com.filmarchive.model.FilmRoll;
import com.filmarchive.model.Photo;
import com.filmarchive.repository.FilmRollRepository;
import com.filmarchive.repository.PhotoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final MinioService minioService;
    private final FilmRollRepository filmRollRepository;

    public PhotoService(PhotoRepository photoRepository, MinioService minioService, FilmRollRepository filmRollRepository) {
        this.photoRepository = photoRepository;
        this.minioService = minioService;
        this.filmRollRepository = filmRollRepository;
    }

    public Page<Photo> findAll(Pageable pageable) {
        return photoRepository.findAll(pageable);
    }

    public Page<Photo> findByFilmRollId(Long filmRollId, Pageable pageable) {
        return photoRepository.findByFilmRollId(filmRollId, pageable);
    }

    public Photo upload(Long filmRollId, MultipartFile file, String caption, String location) {
        FilmRoll roll = filmRollRepository.findById(filmRollId)
                .orElseThrow(() -> new RuntimeException("Film roll not found: " + filmRollId));
        MinioService.UploadResult result = minioService.upload(file, roll.getName());

        int[] dimensions = readDimensions(file);

        Photo photo = new Photo();
        photo.setFilmRollId(filmRollId);
        photo.setStorageKey(result.key());
        photo.setStorageUrl(result.url());
        photo.setCaption(caption);
        photo.setLocation(location);
        photo.setWidth(dimensions[0]);
        photo.setHeight(dimensions[1]);

        return photoRepository.save(photo);
    }

    public void delete(Long id) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Photo not found: " + id));
        minioService.delete(photo.getStorageKey());
        photoRepository.deleteById(id);
    }

    private int[] readDimensions(MultipartFile file) {
        try {
            BufferedImage img = ImageIO.read(file.getInputStream());
            if (img != null) return new int[]{img.getWidth(), img.getHeight()};
        } catch (IOException ignored) {}
        return new int[]{0, 0};
    }
}
