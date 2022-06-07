package com.project.chess.service.impl;

import com.project.chess.exception.BadRequestException;
import com.project.chess.exception.NoContentException;
import com.project.chess.model.Color;
import com.project.chess.model.GameToDtoMapper;
import com.project.chess.model.dto.GameDto;
import com.project.chess.model.dto.MoveRequestDto;
import com.project.chess.model.entity.Figure;
import com.project.chess.model.entity.Game;
import com.project.chess.model.entity.User;
import com.project.chess.repository.FigureRepository;
import com.project.chess.repository.GameRepository;
import com.project.chess.service.FigureService;
import com.project.chess.service.GameService;
import com.project.chess.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;
    private final UserService userService;
    private final FigureRepository figureRepository;
    private final FigureService figureService;
    private final GameToDtoMapper gameToDtoMapper = new GameToDtoMapper();


    public GameServiceImpl(final GameRepository gameRepository,
                           final UserService userService,
                           final FigureRepository figureRepository,
                           final FigureService figureService) {
        this.gameRepository = gameRepository;
        this.userService = userService;
        this.figureRepository = figureRepository;
        this.figureService = figureService;
    }

    @Override
    public GameDto initializeGame(Long gameId, Long player1Id, Long player2Id) throws BadRequestException {
        Game game = gameRepository.findById(gameId).orElseGet(() -> createGame(player1Id, player2Id));

        List<Figure> figures = game.getPlayer1().getFigures();
        figures.addAll(game.getPlayer2().getFigures());

        return gameToDtoMapper.map(game, createBoard(figures));
    }

    @Override
    public GameDto makeMove(Long gameId, MoveRequestDto moveRequestDto) {
        validatePosition(moveRequestDto.getDestPosition());
        validatePosition(moveRequestDto.getInitPosition());
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new NoContentException("Game#" + gameId + " was not found"));

        User player1 = game.getPlayer1();
        User player2 = game.getPlayer2();

        final List<Figure> currentFigures;
        final List<Figure> opponentFigures;
        if(player1.hasTurn()) {
            currentFigures = player1.getFigures();
            opponentFigures = player2.getFigures();
        } else {
            currentFigures = player2.getFigures();
            opponentFigures = player1.getFigures();
        }


        int initY = decipherMoveRequestDto(moveRequestDto.getInitPosition())[0];
        int initX = decipherMoveRequestDto(moveRequestDto.getInitPosition())[1];
        int destY = decipherMoveRequestDto(moveRequestDto.getDestPosition())[0];
        int destX = decipherMoveRequestDto(moveRequestDto.getDestPosition())[1];

        Figure figure = findFigure(currentFigures, initX, initY);
//        for (Figure f : currentFigures) {
//            if (f.isAlive()) {
//                if (f.getX() == initX && f.getY() == initY) {
//                    //check move possibilities
//                    figure = f;
//                    System.out.println("Found figure: " + figure.getName());
//                }
//            }
//        }

        for(Figure f : opponentFigures) {
            if(f.isAlive()) {
                if (f.getX() == destX && f.getY() == destY) {
                    f.setAlive(false);
                    System.out.println("Figure was killed: " + f.getName());
                }
            }
        }

        if(figure == null) {
            throw new BadRequestException("There is no you're figure on " + moveRequestDto.getInitPosition());
        }

        figure.setX(destX);
        figure.setY(destY);

        figureRepository.flush();
        userService.updatePlayers(player1, player2);
        currentFigures.addAll(opponentFigures);
        return gameToDtoMapper.map(game, createBoard(currentFigures));
    }

    @Override
    public List<String> getMoves(Long gameId, MoveRequestDto moveRequestDto) {
        validatePosition(moveRequestDto.getInitPosition());
        Game game = gameRepository.findById(gameId).orElseThrow(
                () -> new NoContentException("Game#" + gameId + " was not found"));

        List<Figure> figures = game.getPlayer1().getFigures();
        figures.addAll(game.getPlayer2().getFigures());

        int initY = decipherMoveRequestDto(moveRequestDto.getInitPosition())[0];
        int initX = decipherMoveRequestDto(moveRequestDto.getInitPosition())[1];

        return figureService.checkMovePosibilities(findFigure(figures, initX, initY), getBoard(figures));
    }

    private char[][] getBoard(List<Figure> figures) {
        char[][] board = new char[8][8];
        for(int i = 0; i < 8; i++) {
            for(int j=0; j < 8; j++) {
                board[i][j] = ' ';
            }
        }

        for (Figure f: figures) {
            board[f.getY()][f.getX()] = 'x';
        }

        return board;
    }

    private Figure findFigure(List<Figure> figures, int x, int y) {
        for (Figure f : figures) {
            if (f.isAlive()) {
                if (f.getX() == x && f.getY() == y) {
                    //check move possibilities
                    System.out.println("Found figure: " + f.getName());
                    return f;
                }
            }
        }

        return null;
    }

    private void validatePosition(String position) {
        List<Character> allowedChars = List.of('A', 'B', 'C', 'D', 'E', 'E', 'F', 'G', 'H');
        boolean firstPosition = allowedChars.contains(position.charAt(0));
        boolean secondPosition = Character.getNumericValue(position.charAt(1)) >= 1
                && Character.getNumericValue(position.charAt(1)) <= 8;

        if (!firstPosition || !secondPosition) {
            throw new BadRequestException("Inserted position: " + position + " has wrong format.\n" +
                    "Example of acceptable format for a position: 'A1'.");
        }
    }

    private int[] decipherMoveRequestDto(String position) {
        int[] decipheredPosition = new int[2];
        switch(position.charAt(0)){
            case 'A':
                decipheredPosition[0] = 0;
                break;
            case 'B':
                decipheredPosition[0] = 1;
                break;
            case 'C':
                decipheredPosition[0] = 2;
                break;
            case 'D':
                decipheredPosition[0] = 3;
                break;
            case 'E':
                decipheredPosition[0] = 4;
                break;
            case 'F':
                decipheredPosition[0] = 5;
                break;
            case 'G':
                decipheredPosition[0] = 6;
                break;
            default:
                decipheredPosition[0] = 7;
        }

        decipheredPosition[1] = Character.getNumericValue(position.charAt(1)) - 1;
        return decipheredPosition;
    }

    private Game createGame(Long player1Id, Long player2Id) throws BadRequestException {
        System.out.println("Creating new game");
        Game game = new Game();

        User player1 = userService.findOrCreatePlayer("Guest#" +
                        (int) Math.floor(Math.random()*10000000)
                , player1Id);

        User player2 = userService.findOrCreatePlayer("Guest#" +
                (int) Math.floor(Math.random()*10000000), player2Id);

        validateUser(player1, game);
        validateUser(player2, game);

        gameRepository.save(game);

        player1.setGame(game);
        player2.setGame(game);

        Color player1Color = Math.random() > 0.5 ? Color.WHITE : Color.BLACK;
        Color player2Color = Color.WHITE.equals(player1Color) ? Color.BLACK : Color.WHITE;

        if(player1Color == Color.WHITE) {
            player1.setHasTurn(true);
            player2.setHasTurn(false);
        } else {
            player2.setHasTurn(true);
            player1.setHasTurn(false);
        }

        userService.createInitialSetOfFigures(player1, player1Color);
        userService.createInitialSetOfFigures(player2, player2Color);

        game.setPlayer1(player1);
        game.setPlayer2(player2);

        gameRepository.flush();
        return game;
    }

    private void validateUser(User user, Game game) {
        System.out.println("Validating user");
        if(user.getGame() != null && !user.getGame().getId().equals(game.getId())) {
            System.out.println(user.getId() + " " + game.getId());
            throw new BadRequestException("User + " + user.getId() + " is already in different game");
        }
    }

    private String createBoard(List<Figure> figures) {
        String[][] board = new String[8][8];

        StringBuilder builder = new StringBuilder();
        builder.append(("    A  B  C  D  E  F  G  H\n"));
        for (int i = 0; i < board.length; i++) {
            builder.append(i + 1).append("  ");
            for (int j = 0; j < board.length; j++){
                builder.append("[").append(getFigure(i, j, figures)).append("]");
            }
            builder.append("\n");
        }

        return builder.toString();
    }

    private char getFigure(int x, int y, List<Figure> figures) {
        for (Figure figure: figures) {
            if(figure.getX() == x && figure.getY() == y && figure.isAlive()) {
                String name = figure.getName();
                switch (figure.getColor()) {
                    case WHITE:
                        return name.toLowerCase().charAt(0);
                    case BLACK:
                        return name.toUpperCase().charAt(0);
                }
            }
        }
        return ' ';
    }
}
