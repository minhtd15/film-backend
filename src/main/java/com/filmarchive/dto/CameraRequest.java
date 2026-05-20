package com.filmarchive.dto;

import jakarta.validation.constraints.NotBlank;

public record CameraRequest(
        @NotBlank String name
) {}
