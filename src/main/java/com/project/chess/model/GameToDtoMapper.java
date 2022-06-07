package com.project.chess.model;

import com.project.chess.model.dto.GameDto;
import com.project.chess.model.entity.Game;
import com.project.chess.model.entity.User;

import java.util.List;
import java.util.stream.Collectors;

public class GameToDtoMapper {
    public GameDto map(Game game, String board, String winner) {
        String player1 = game.getPlayer1().getName() + " as " + game.getPlayer1().getColor();
        String player2 = game.getPlayer2().getName() + " as " + game.getPlayer2().getColor();
        GameDto gameDto = new GameDto();
        gameDto.setGameId(game.getId());
        gameDto.setPlayers(List.of(player1, player2));
        gameDto.setBoard(board);
        gameDto.setCurrentTurn(game.getPlayer1().hasTurn() ? game.getPlayer1().getName() : game.getPlayer2().getName());
        gameDto.setWinner(winner);

        return gameDto;
    }
}
