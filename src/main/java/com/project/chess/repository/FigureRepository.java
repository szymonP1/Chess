package com.project.chess.repository;

import com.project.chess.model.entity.Figure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FigureRepository extends JpaRepository<Figure, Long> {
}
