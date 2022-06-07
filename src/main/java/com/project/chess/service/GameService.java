package com.project.chess.service;

import com.project.chess.model.dto.GameDto;
import com.project.chess.model.dto.MoveRequestDto;
import com.project.chess.model.entity.User;

import java.util.List;

public interface GameService {
    GameDto initializeGame (Long gameId, Long player1Id, Long player2Id);

    GameDto makeMove(Long gameId, MoveRequestDto moveRequestDto);

    List<String> getMoves(Long gameId, MoveRequestDto moveRequestDto);
}
