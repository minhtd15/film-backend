package com.filmarchive.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "photos")
@Getter @Setter
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "film_roll_id", nullable = false)
    private Long filmRollId;

    @Column(name = "storage_url", nullable = false, length = 500)
    private String storageUrl;

    @Column(name = "storage_key", nullable = false, length = 500)
    private String storageKey;

    @Column(length = 255)
    private String caption;

    @Column(length = 255)
    private String location;

    private Integer width;

    private Integer height;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
