package com.project.chess.service.impl;

import com.project.chess.model.Color;
import com.project.chess.model.entity.Figure;
import com.project.chess.service.FigureService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FigureServiceImpl implements FigureService {
    private final static String MOVE = "Move: ";
    private final static String ATTACK = "Attack: ";

    @Override
    public List<String> checkMovePosibilities(Figure figure, char[][] board) {
        if("PAWN".equals(figure.getName())) {
            System.out.println(figure.getX());
            System.out.println(figure.getY());
            return getPawnMovesFromLocation(figure.getColor(), figure.getX(), figure.getY(), board);
        }
        return null;
    }

    private List<String> getPawnMovesFromLocation(Color color, int x, int y, char[][] board) {
        List<String> moves = new ArrayList<>();
        List<String> attacks = new ArrayList<>();
        System.out.println(color);
        System.out.println(x + " " + y);
        if(color == Color.WHITE) {
            if(board[y + 1][x] == 'x') {
                moves.add(MOVE + ((char)(y + 65)) + (x + 2));
            }
            if(y > 0) {
                attacks.add(ATTACK + ((char)(y + 65 - 1)) + (x + 2));
            }
            if(y < 7) {
                attacks.add(ATTACK + ((char)(y + 65 + 1)) + (x + 2));
            }
        } else {
            if(board[y - 1][x] == 'x') {
                moves.add(MOVE + ((char)(y + 65)) + (x));
            }
            if(y > 0) {
                attacks.add(ATTACK + ((char)(y + 65 - 1)) + (x));
            }
            if(y < 7) {
                attacks.add(ATTACK + ((char) (y + 65 + 1)) + (x));
            }
        }
        moves.addAll(attacks);
        return moves;
    }
}
