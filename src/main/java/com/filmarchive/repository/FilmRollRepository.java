package com.filmarchive.repository;

import com.filmarchive.model.FilmRoll;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmRollRepository extends JpaRepository<FilmRoll, Long> {
}
