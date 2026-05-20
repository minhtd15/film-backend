package com.filmarchive.service;

import com.filmarchive.dto.FilmRollRequest;
import com.filmarchive.model.FilmRoll;
import com.filmarchive.repository.FilmRollRepository;
import com.filmarchive.repository.PhotoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FilmRollService {

    private final FilmRollRepository filmRollRepository;
    private final PhotoRepository photoRepository;
    private final MinioService minioService;

    public FilmRollService(FilmRollRepository filmRollRepository, PhotoRepository photoRepository, MinioService minioService) {
        this.filmRollRepository = filmRollRepository;
        this.photoRepository = photoRepository;
        this.minioService = minioService;
    }

    public List<FilmRoll> findAll() {
        return filmRollRepository.findAll();
    }

    public FilmRoll findById(Long id) {
        return filmRollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Film roll not found: " + id));
    }

    public FilmRoll create(FilmRollRequest request) {
        FilmRoll roll = new FilmRoll();
        applyRequest(roll, request);
        FilmRoll saved = filmRollRepository.save(roll);

        try {
            String slug = saved.getName().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
            minioService.createDefaultFolder(slug + "/");
        } catch (Exception e) {
            // folder creation is best-effort; photos will still create the folder on upload
        }

        return saved;
    }

    public FilmRoll update(Long id, FilmRollRequest request) {
        FilmRoll roll = findById(id);
        applyRequest(roll, request);
        return filmRollRepository.save(roll);
    }

    public void delete(Long id) {
        filmRollRepository.deleteById(id);
    }

    public Map<Long, Long> photoCountByRoll() {
        return filmRollRepository.findAll().stream()
                .collect(Collectors.toMap(
                        FilmRoll::getId,
                        r -> photoRepository.countByFilmRollId(r.getId())
                ));
    }

    private void applyRequest(FilmRoll roll, FilmRollRequest req) {
        roll.setName(req.name());
        roll.setManufacturer(req.manufacturer());
        roll.setIso(req.iso());
        roll.setFilmFormat(req.filmFormat());
        roll.setColorType(req.colorType());
        roll.setDate(req.date());
        roll.setDateType(req.dateType());
        roll.setCameraId(req.cameraId());
    }
}
