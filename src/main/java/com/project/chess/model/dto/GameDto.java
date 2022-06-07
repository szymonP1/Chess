package com.project.chess.model.dto;

import java.util.Arrays;
import java.util.List;

public class GameDto {
    private List<String> players;
    private String board;
    private Long gameId;
    private String currentTurn;
    private String winner;

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(String currentTurn) {
        this.currentTurn = currentTurn;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Game #").append(gameId).append('\n');
        builder.append("Players: [").append(players.get(0)).append(" vs ").append(players.get(1)).append("]\n");
        builder.append("LowerCase = WHITES, UpperCase = BLACKS\n");
        builder.append("p = pawn, r = rook, h = knight, b = bishop, k = king, q = queen\n");
        if (winner != null) {
            builder.append("Check Mate! ").append(winner).append(" won! Grats!").append("\n\n");
        } else {
            builder.append("Current turn: ").append(currentTurn).append("\n\n");
        }
        builder.append(board);

        return builder.toString();
    }
}
