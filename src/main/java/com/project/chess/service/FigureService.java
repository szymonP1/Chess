package com.project.chess.service;

import com.project.chess.model.dto.PossibleMovesDto;
import com.project.chess.model.entity.Figure;

import java.util.List;

public interface FigureService {

    PossibleMovesDto checkMovePosibilities(Figure figure, char[][] board);
    PossibleMovesDto checkMovePosibilities(Figure figure, char[][] board, boolean forEnemyPawn);
}
