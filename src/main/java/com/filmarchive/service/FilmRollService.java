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

    public FilmRollService(FilmRollRepository filmRollRepository, PhotoRepository photoRepository) {
        this.filmRollRepository = filmRollRepository;
        this.photoRepository = photoRepository;
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
        return filmRollRepository.save(roll);
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
