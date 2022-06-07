package com.project.chess.service.impl;

import com.project.chess.model.Color;
import com.project.chess.model.dto.PossibleMovesDto;
import com.project.chess.model.entity.Figure;
import com.project.chess.service.FigureService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

@Service
public class FigureServiceImpl implements FigureService {

    @Override
    public PossibleMovesDto checkMovePosibilities(Figure figure, char[][] board) {
        final PossibleMovesDto movesDto = new PossibleMovesDto();
        movesDto.setFigureName(figure.getColor() + " " + figure.getName());
        if("PAWN".equals(figure.getName())) {
            movesDto.setMoves(getPawnMovesFromLocation(figure.getColor(), figure.getX(), figure.getY(), board, false));
        }
        if("HORSE".equals(figure.getName())) {
            movesDto.setMoves(getKnightMovesFromLocation(figure.getColor(), figure.getX(), figure.getY(), board));
        }
        if("ROOK".equals(figure.getName())) {
            movesDto.setMoves(getRookMovesFromLocation(figure.getColor(), figure.getX(), figure.getY(), board));
        }
        if("BISHOP".equals(figure.getName())) {
            movesDto.setMoves(getBishopMovesFromLocation(figure.getColor(), figure.getX(), figure.getY(), board));
        }
        if ("QUEEN".equals(figure.getName())) {
            movesDto.setMoves(getQueenMovesFromLocation(figure.getColor(), figure.getX(), figure.getY(), board));
        }
        if ("KING".equals(figure.getName())) {
            movesDto.setMoves(getKingMovesFromLocation(figure.getColor(), figure.getX(), figure.getY(), board));
        }

        return movesDto;
    }

    @Override
    public PossibleMovesDto checkMovePosibilities(Figure figure, char[][] board, boolean forEnemyPawn) {
        final PossibleMovesDto movesDto = new PossibleMovesDto();
        movesDto.setFigureName(figure.getColor() + " " + figure.getName());
        if ("PAWN".equals(figure.getName())) {
            movesDto.setMoves(getPawnMovesFromLocation(figure.getColor(), figure.getX(), figure.getY(), board, forEnemyPawn));
        }

        return movesDto;
    }

    private List<String> getKingMovesFromLocation(Color color, int p, int q, char[][] board) {
        List<String> moves = new ArrayList<>();
        int[] X = {-1, 1, 0, 0, -1, -1, 1, 1};
        int[] Y = {0, 0, 1, -1, 1, -1, -1, 1};
        char symbolOfAlies = color.equals(Color.WHITE) ? 'W' : 'B';

        for (int i = 0; i < 8; i++) {
            int x = p + X[i];
            int y = q + Y[i];

            if (x >= 0 && y >= 0 && x < 8 && y < 8 && board[y][x] != symbolOfAlies) {
                moves.add("" + ((char) (y + 65)) + (x + 1));
            }
        }

        return moves;
    }

    private List<String> getQueenMovesFromLocation(Color color, int x, int y, char[][] board) {
        List<String> moves = getBishopMovesFromLocation(color, x, y, board);
        moves.addAll(getRookMovesFromLocation(color, x, y, board));

        return moves;
    }

    private List<String> getBishopMovesFromLocation(Color color, int x, int y, char[][] board) {
        List<String> moves = new ArrayList<>();
        char symbolOfAlies = color.equals(Color.WHITE) ? 'W' : 'B';
        char symbolOfEnemies = color.equals(Color.WHITE) ? 'B' : 'W';
        boolean shouldBreak = false;

        for(int i = x - 1; i >= 0; i--) {
            for (int j = y; j < 8; j++) {
                if (abs(x - i) == abs(y - j)) {
                    if ((i - 1 >= 0 && j + 1 <= 7) &&
                            (board[j + 1][i - 1] == symbolOfAlies || board[j][i] == symbolOfAlies)) {
                        shouldBreak = true;
                        break;
                    }
                    moves.add("" + (char) (65 + j) + (i + 1));
                    if (board[j][i] == symbolOfEnemies) {
                        shouldBreak = true;
                        break;
                    }
                }
            }
            if (shouldBreak) break;
        }

        shouldBreak = false;
        for(int i = x - 1; i >= 0; i--) {
            for (int j = y; j >= 0; j--) {
                if (abs(x - i) == abs(y - j)) {
                    if ((i - 1 >= 0 && j - 1 >= 0) &&
                            (board[j - 1][i - 1] == symbolOfAlies || board[j][i] == symbolOfAlies)) {
                        shouldBreak = true;
                        break;
                    }
                    moves.add("" + (char) (65 + j) + (i + 1));
                    if (board[j][i] == symbolOfEnemies) {
                        shouldBreak = true;
                        break;
                    }
                }
            }
            if (shouldBreak) break;
        }

        shouldBreak = false;
        for(int i = x +1; i < 8; i++) {
            for (int j = y; j >= 0; j--) {
                if (abs(x - i) == abs(y - j)) {
                    if ((i + 1 <= 7 && j - 1 >= 0) &&
                            (board[j - 1][i + 1] == symbolOfAlies || board[j][i] == symbolOfAlies)) {
                        shouldBreak = true;
                        break;
                    }
                    moves.add("" + (char) (65 + j) + (i + 1));
                    if (board[j][i] == symbolOfEnemies) {
                        shouldBreak = true;
                        break;
                    }
                }
            }
            if (shouldBreak) break;
        }

        shouldBreak = false;
        for(int i = x +1; i < 8; i++) {
            for (int j = y; j < 8; j++) {
                if (abs(x - i) == abs(y - j)) {
                    if ((i + 1 <= 7 && j + 1 <= 7) &&
                            (board[j + 1][i + 1] == symbolOfAlies || board[j][i] == symbolOfAlies)) {
                        shouldBreak = true;
                        break;
                    }
                    moves.add("" + (char) (65 + j) + (i + 1));
                    if (board[j][i] == symbolOfEnemies) {
                        shouldBreak = true;
                        break;
                    }
                }
            }
            if (shouldBreak) break;
        }

        return moves;
    }

    private List<String> getRookMovesFromLocation(Color color, int x, int y, char[][] board) {
        List<String> moves = new ArrayList<>();
        char symbolOfAlies = color.equals(Color.WHITE) ? 'W' : 'B';
        char symbolOfEnemies = color.equals(Color.WHITE) ? 'B' : 'W';

        //check right
        for(int i = y; i < 8; i++) {
            if(y != i){
                moves.add("" + (char)(65 + i) + (x+1));
            }
            if(board[i][x] == symbolOfEnemies) {
                break;
            }
            if(i + 1 < 8 && board[i + 1][x] == symbolOfAlies) {
                break;
            }
        }

        //check down
        for(int i = x; i < 8; i++) {
            if(x != i){
                moves.add("" + (char)(y + 65) + (1 + i));
            }
            if(board[y][i] == symbolOfEnemies) {
                break;
            }
            if(i + 1 < 8 && board[y][i + 1] == symbolOfAlies) {
                break;
            }
        }

        //check left
        for(int i = y; i >= 0; i--) {
            if(y != i){
                moves.add("" + (char)(i + 65) + (x+1));
            }
            if(board[i][x] == symbolOfEnemies) {
                break;
            }
            if(i - 1 > 0 && board[i - 1][x] == symbolOfAlies) {
                break;
            }
        }

        //check up
        for(int i = x; i >= 0; i--) {
            if(x != i){
                moves.add("" + (char)(y + 65) + (i + 1));
            }
            if(board[y][i] == symbolOfEnemies) {
                break;
            }
            if(i - 1 > 0 && board[y][i - 1] == symbolOfAlies) {
                break;
            }
        }

        return moves;
    }

    private List<String> getKnightMovesFromLocation(Color color, int p, int q, char[][] board) {
        List<String> moves = new ArrayList<>();
        int[] X = { 2, 1, -1, -2, -2, -1, 1, 2 };
        int[] Y = { 1, 2, 2, 1, -1, -2, -2, -1 };
        char symbolOfAlies = color.equals(Color.WHITE) ? 'W' : 'B';

        for (int i = 0; i < 8; i++) {

            int x = p + X[i];
            int y = q + Y[i];

            if (x >= 0 && y >= 0 && x < 8 && y < 8 && board[y][x] != symbolOfAlies) {
                moves.add("" + ((char) (y + 65)) + (x + 1));
            }
        }
        return moves;
    }

    private List<String> getPawnMovesFromLocation(Color color, int x, int y, char[][] board, boolean forEnemyPawn) {
        List<String> moves = new ArrayList<>();
        char symbolOfEnemies = color.equals(Color.WHITE) ? 'B' : 'W';

        if (color == Color.WHITE) {
            if (x + 1 <= 7 && board[y][x + 1] == ' ' && !forEnemyPawn) {
                moves.add("" + ((char) (y + 65)) + (x + 2));
            }
            if (y > 0 && (board[y - 1][x + 1] == symbolOfEnemies || forEnemyPawn)) {
                moves.add("" + ((char) (y + 65 - 1)) + (x + 2));
            }
            if (y < 7 && (board[y + 1][x + 1] == symbolOfEnemies || forEnemyPawn)) {
                moves.add("" + ((char) (y + 65 + 1)) + (x + 2));
            }
        } else {
            if (x - 1 >= 0 && board[y][x - 1] == ' ' && !forEnemyPawn) {
                moves.add("" + ((char) (y + 65)) + (x));
            }
            if (y > 0 && (board[y - 1][x - 1] == symbolOfEnemies || forEnemyPawn)) {
                moves.add("" + (char) (y + 65 - 1) + (x));
            }
            if (y < 7 && (board[y + 1][x - 1] == symbolOfEnemies || forEnemyPawn)) {
                moves.add("" + (char) (y + 65 + 1) + (x));
            }
        }

        return moves;
    }
}
