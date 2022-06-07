package com.project.chess.service.impl;

import com.project.chess.exception.BadRequestException;
import com.project.chess.exception.NoContentException;
import com.project.chess.model.Color;
import com.project.chess.model.GameToDtoMapper;
import com.project.chess.model.dto.GameDto;
import com.project.chess.model.dto.MoveRequestDto;
import com.project.chess.model.dto.PossibleMovesDto;
import com.project.chess.model.entity.Figure;
import com.project.chess.model.entity.Game;
import com.project.chess.model.entity.User;
import com.project.chess.repository.FigureRepository;
import com.project.chess.repository.GameRepository;
import com.project.chess.service.FigureService;
import com.project.chess.service.GameService;
import com.project.chess.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        return gameToDtoMapper.map(game, createBoard(figures), null);
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
        User potentialWinner = null;
        
        if(player1.hasTurn()) {
            currentFigures = player1.getFigures();
            opponentFigures = player2.getFigures();
            potentialWinner = player2;
        } else {
            currentFigures = player2.getFigures();
            opponentFigures = player1.getFigures();
            potentialWinner = player1;
        }

        String kingsLocation = getKingsLocation(currentFigures);
        int initY = decipherMoveRequestDto(moveRequestDto.getInitPosition())[0];
        int initX = decipherMoveRequestDto(moveRequestDto.getInitPosition())[1];
        int destY = decipherMoveRequestDto(moveRequestDto.getDestPosition())[0];
        int destX = decipherMoveRequestDto(moveRequestDto.getDestPosition())[1];

        Figure figure = findFigure(currentFigures, initX, initY, moveRequestDto.getInitPosition(), false);

        List<String> possibleMoves = getMoves(gameId, moveRequestDto).getMoves();
        if (!possibleMoves.contains(moveRequestDto.getDestPosition())) {
            throw new BadRequestException(figure.getName() + " can't move to desired position." + getPossibleMovesDescription(possibleMoves));
        }

        Figure killedFigure = findFigure(opponentFigures, destX, destY, moveRequestDto.getInitPosition(), true);
        if (killedFigure != null) {
            killedFigure.setAlive(false);
            System.out.println("Figure was killed: " + killedFigure.getName());
            opponentFigures.remove(killedFigure);
        }

        List<Figure> allFigures = Stream.of(currentFigures, opponentFigures)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        if(checkForMate(opponentFigures, currentFigures)) {
            userService.endGameUpdates(player1, player2);
            return gameToDtoMapper.map(game, createBoard(allFigures), potentialWinner.getName());
        }

        figure.setX(destX);
        figure.setY(destY);

        checkForChecks(moveRequestDto.getDestPosition(),  currentFigures, opponentFigures, kingsLocation, figure, false);

        if(killedFigure != null && "KING".equals(killedFigure.getName())) {
            userService.endGameUpdates(player1, player2);
            return gameToDtoMapper.map(game, createBoard(allFigures), potentialWinner.getName());
        }

        figureRepository.flush();
        userService.updatePlayers(player1, player2);
        return gameToDtoMapper.map(game, createBoard(allFigures), null);
    }

    @Override
    public PossibleMovesDto getMoves(Long gameId, MoveRequestDto moveRequestDto) {
        validatePosition(moveRequestDto.getInitPosition());
        Game game = gameRepository.findById(gameId).orElseThrow(
                () -> new NoContentException("Game#" + gameId + " was not found"));

        List<Figure> allFigures = Stream.of(game.getPlayer1().getFigures(), game.getPlayer2().getFigures())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        int initY = decipherMoveRequestDto(moveRequestDto.getInitPosition())[0];
        int initX = decipherMoveRequestDto(moveRequestDto.getInitPosition())[1];
        final PossibleMovesDto possibleMovesDto = figureService.checkMovePosibilities(findFigure(allFigures, initX, initY, moveRequestDto.getInitPosition(), false), getBoard(allFigures));
        possibleMovesDto.setInitialPosition(moveRequestDto.getInitPosition());
        return possibleMovesDto;
    }

    private boolean checkForChecks(String destPosition, List<Figure> currentFigures, List<Figure> opponentFigures, String kingsLocation, Figure king, boolean lookingForMate) {
        if(king != null && "KING".equals(king.getName())) {
            if (checkForCheck(true, currentFigures, opponentFigures, destPosition, kingsLocation)) {
                if(lookingForMate) {
                    return true;
                } else {
                    throw new BadRequestException("It would be check!");
                }
            }
        } else {
            if (checkForCheck(false,  currentFigures, opponentFigures, destPosition, kingsLocation)) {
                if(lookingForMate) {
                    return true;
                } else {
                    throw new BadRequestException("It would be check!");
                }
            }
        }
        return false;
    }

    private StringBuilder getPossibleMovesDescription(List<String> possibleMoves) {
        StringBuilder builder = new StringBuilder();
        builder.append("\nPossible moves: \n");
        for (String s : possibleMoves) {
            builder.append(s).append("\n");
        }
        return builder;
    }

    private String getKingsLocation(List<Figure> figures) {
        for (Figure f: figures) {
            if("KING".equals(f.getName())) {
                return "" + (char)(f.getY() + 65) + (f.getX() + 1);
            }
        }
        return "";
    }

    private char[][] getBoard(List<Figure> figures) {
        char[][] board = new char[8][8];
        for(int i = 0; i < 8; i++) {
            for(int j=0; j < 8; j++) {
                board[i][j] = ' ';
            }
        }

        for (Figure f: figures) {
            if (f.isAlive()) {
                board[f.getY()][f.getX()] = f.getColor().equals(Color.WHITE) ? 'W' : 'B';
            }
        }

        return board;
    }

    private Figure findFigure(List<Figure> figures, int x, int y, String initPosition, boolean lookingForKill) {
        for (Figure f : figures) {
            if (f.isAlive()) {
                if (f.getX() == x && f.getY() == y) {
                    //check move possibilities
                    System.out.println("Found figure: " + f.getColor() + " " + f.getName());
                    return f;
                }
            }
        }

        if(!lookingForKill) {
            throw new BadRequestException("There is no you're figure on " + initPosition);
        } else {
            return null;
        }
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
            throw new BadRequestException("User " + user.getId() + " is already in different game");
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

    private boolean checkForCheck(boolean isKing, List<Figure> alliesFigures, List<Figure> opponentFigures, String destination, String kingsLocation) {
        List<Figure> allFigures = Stream.of(alliesFigures, opponentFigures)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        char[][] getBoard = getBoard(allFigures);
        List<String> opponentsMoves = new ArrayList<>();

        getOpponentMoves(opponentFigures, getBoard, opponentsMoves);
        if (isKing) {
            return opponentsMoves.contains(destination);
        }

        return opponentsMoves.contains(kingsLocation);
    }

    private Figure findCheckingFigure(List<Figure> opponentFigures, String destination, char[][] getBoard, List<String> opponentsMoves) {
        if(opponentsMoves.contains(destination)) {
            for(Figure f: opponentFigures) {
                if (figureService.checkMovePosibilities(f, getBoard).getMoves().contains(destination)) {
                    return f;
                }
            }
        }
        return null;
    }

    private boolean checkForMate(List<Figure> opponentFigures, List<Figure> alliesFigures) {
        List<Figure> allFigures = Stream.of(alliesFigures, opponentFigures)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        char[][] board = getBoard(allFigures);
        Figure king = alliesFigures.stream().filter(figure -> "KING".equals(figure.getName())).findFirst().get();
        List<String> kingsMoves = figureService.checkMovePosibilities(king, board).getMoves();
        List<String> opponentsMoves = new ArrayList<>();

        getOpponentMoves(opponentFigures, board, opponentsMoves);
        boolean isCheckMate = true;
        for(String s : kingsMoves) {
            if(!checkForChecks(s, alliesFigures, opponentFigures, getKingsLocation(alliesFigures), king, true)) {
                isCheckMate = false;
            }
        }

        if(isCheckMate) {
            isCheckMate = false;
            for(String s: kingsMoves){
                Figure checkingFigure = findCheckingFigure(opponentFigures, s, board, opponentsMoves);
                if(getCheckMatingMoves(checkingFigure, board, alliesFigures).contains(s)) {
                   isCheckMate = true;
                }
            }
        }

        return isCheckMate && kingsMoves.size() > 0;
    }

    private List<String> getCheckMatingMoves(Figure checkingFigure, char[][] board, List<Figure> alliedFigures) {
        List<String> blockingMoves = new ArrayList<>();
        List<String> checkingMoves = figureService.checkMovePosibilities(checkingFigure, board).getMoves();
        for (Figure f: alliedFigures) {
            if ("PAWN".equals(f.getName())) {
                blockingMoves.addAll(figureService.checkMovePosibilities(f, board, true).getMoves());
            } else {
                blockingMoves.addAll(figureService.checkMovePosibilities(f, board).getMoves());
            }
        }

        for(String s : blockingMoves) {
            checkingMoves.remove(s);
        }

        return checkingMoves;
    }

    private void getOpponentMoves(List<Figure> opponentFigures, char[][] getBoard, List<String> opponentsMoves) {
        for (Figure f: opponentFigures) {
            if ("PAWN".equals(f.getName())) {
                opponentsMoves.addAll(figureService.checkMovePosibilities(f, getBoard, true).getMoves());
            } else {
                opponentsMoves.addAll(figureService.checkMovePosibilities(f, getBoard).getMoves());
            }
        }
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
