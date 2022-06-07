package com.project.chess.model.dto;

import java.util.ArrayList;
import java.util.List;

public class PossibleMovesDto {
    private String figureName;
    private String initialPosition;
    private List<String> moves = new ArrayList<>();

    public String getFigureName() {
        return figureName;
    }

    public void setFigureName(String figureName) {
        this.figureName = figureName;
    }

    public String getInitialPosition() {
        return initialPosition;
    }

    public void setInitialPosition(String initialPosition) {
        this.initialPosition = initialPosition;
    }

    public List<String> getMoves() {
        return moves;
    }

    public void setMoves(List<String> moves) {
        this.moves = moves;
    }
}
