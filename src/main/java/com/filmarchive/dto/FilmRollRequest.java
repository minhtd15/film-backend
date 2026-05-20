package com.filmarchive.dto;

import com.filmarchive.model.FilmRoll.ColorType;
import com.filmarchive.model.FilmRoll.DateType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record FilmRollRequest(
        @NotBlank String name,
        @NotBlank String manufacturer,
        @NotNull Integer iso,
        @NotBlank String filmFormat,
        @NotNull ColorType colorType,
        @NotNull LocalDate date,
        @NotNull DateType dateType,
        @NotNull Long cameraId
) {}
