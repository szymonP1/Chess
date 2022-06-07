package com.project.chess.service;

import com.project.chess.model.entity.Figure;

import java.util.List;

public interface FigureService {

    List<String> checkMovePosibilities(Figure figure, char[][] board);
}
