package com.filmarchive.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "film_rolls")
@Getter @Setter
public class FilmRoll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String manufacturer;

    @Column(nullable = false)
    private Integer iso;

    @Column(name = "film_format", nullable = false, length = 20)
    private String filmFormat;

    @Enumerated(EnumType.STRING)
    @Column(name = "color_type", nullable = false)
    private ColorType colorType = ColorType.color;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "date_type", nullable = false)
    private DateType dateType;

    @Column(name = "camera_id")
    private Long cameraId;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum ColorType { color, black_and_white, slide }
    public enum DateType { in, out }
}
