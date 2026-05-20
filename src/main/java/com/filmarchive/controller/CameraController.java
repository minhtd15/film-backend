package com.filmarchive.controller;

import com.filmarchive.dto.CameraRequest;
import com.filmarchive.model.Camera;
import com.filmarchive.service.CameraService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cameras")
public class CameraController {

    private final CameraService cameraService;

    public CameraController(CameraService cameraService) {
        this.cameraService = cameraService;
    }

    @GetMapping
    public List<Camera> findAll() {
        return cameraService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Camera create(@Valid @RequestBody CameraRequest request) {
        return cameraService.create(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        cameraService.delete(id);
    }
}
