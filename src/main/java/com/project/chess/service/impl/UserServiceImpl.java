package com.project.chess.service.impl;

import com.project.chess.exception.BadRequestException;
import com.project.chess.model.Color;
import com.project.chess.model.entity.Figure;
import com.project.chess.model.entity.User;
import com.project.chess.model.entity.figures.*;
import com.project.chess.repository.FigureRepository;
import com.project.chess.repository.UserRepository;
import com.project.chess.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FigureRepository figureRepository;

    public UserServiceImpl(final UserRepository userRepository,
                           final FigureRepository figureRepository) {
        this.userRepository = userRepository;
        this.figureRepository = figureRepository;
    }

    @Override
    public User findOrCreatePlayer(String name, Long userId) {
        return userRepository.findById(userId).orElseGet(() -> createPlayer(name));
    }

    @Override
    public String createNewPlayer(String name) {
        return "Player: " + createPlayer(name).getName() + " was created";
    }

    @Override
    public void createInitialSetOfFigures(User user, Color color) {
        final List<Figure> initialList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            Pawn pawn = new Pawn();
            pawn.setY(i);
            pawn.setX(color == Color.WHITE ? 1 : 6);
            pawn.setColor(color);
            pawn.setAlive(true);
            figureRepository.save(pawn);
            initialList.add(pawn);
        }

        Bishop bishop1 = new Bishop();
        bishop1.setY(2);
        bishop1.setX(color == Color.WHITE ? 0 : 7);
        bishop1.setColor(color);
        bishop1.setAlive(true);
        figureRepository.save(bishop1);
        initialList.add(bishop1);

        Bishop bishop2 = new Bishop();
        bishop2.setY(5);
        bishop2.setX(color == Color.WHITE ? 0 : 7);
        bishop2.setColor(color);
        bishop2.setAlive(true);
        figureRepository.save(bishop2);
        initialList.add(bishop2);

        Horse horse1 = new Horse();
        horse1.setY(6);
        horse1.setX(color == Color.WHITE ? 0 : 7);
        horse1.setColor(color);
        horse1.setAlive(true);
        figureRepository.save(horse1);
        initialList.add(horse1);

        Horse horse2 = new Horse();
        horse2.setY(1);
        horse2.setX(color == Color.WHITE ? 0 : 7);
        horse2.setColor(color);
        horse2.setAlive(true);
        figureRepository.save(horse2);
        initialList.add(horse2);

        Rook rook1 = new Rook();
        rook1.setY(0);
        rook1.setX(color == Color.WHITE ? 0 : 7);
        rook1.setColor(color);
        rook1.setAlive(true);
        figureRepository.save(rook1);
        initialList.add(rook1);

        Rook rook2 = new Rook();
        rook2.setY(7);
        rook2.setX(color == Color.WHITE ? 0 : 7);
        rook2.setColor(color);
        rook2.setAlive(true);
        figureRepository.save(rook2);
        initialList.add(rook2);

        Queen queen = new Queen();
        queen.setY(color == Color.WHITE ? 3 : 4);
        queen.setX(color == Color.WHITE ? 0 : 7);
        queen.setColor(color);
        queen.setAlive(true);
        figureRepository.save(queen);
        initialList.add(queen);

        King king = new King();
        king.setY(color == Color.WHITE ? 4 : 3);
        king.setX(color == Color.WHITE ? 0 : 7);
        king.setColor(color);
        king.setAlive(true);
        figureRepository.save(king);
        initialList.add(king);
        user.setFigures(initialList);
        user.setColor(color);

        userRepository.flush();
    }

    @Override
    public void updatePlayers(User player1, User player2) {
        if(player1.hasTurn()) {
            player2.setHasTurn(true);
            player1.setHasTurn(false);
        } else {
            player2.setHasTurn(false);
            player1.setHasTurn(true);
        }

        userRepository.flush();
    }

    private User createPlayer(String name) {
        if (userRepository.findByName(name).isPresent()) {
            throw new BadRequestException("User with name: " + name + " already exists");
        }

        User user = new User();
        user.setName(name);
        userRepository.save(user);

        return user;
    }
}
