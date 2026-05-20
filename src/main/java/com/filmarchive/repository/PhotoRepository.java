package com.filmarchive.repository;

import com.filmarchive.model.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    Page<Photo> findByFilmRollId(Long filmRollId, Pageable pageable);
    long countByFilmRollId(Long filmRollId);
}
