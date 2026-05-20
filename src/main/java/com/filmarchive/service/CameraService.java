package com.filmarchive.service;

import com.filmarchive.dto.CameraRequest;
import com.filmarchive.model.Camera;
import com.filmarchive.repository.CameraRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CameraService {

    private final CameraRepository cameraRepository;

    public CameraService(CameraRepository cameraRepository) {
        this.cameraRepository = cameraRepository;
    }

    public List<Camera> findAll() {
        return cameraRepository.findAll();
    }

    public Camera create(CameraRequest request) {
        Camera camera = new Camera();
        camera.setName(request.name());
        return cameraRepository.save(camera);
    }

    public void delete(Long id) {
        cameraRepository.deleteById(id);
    }
}
