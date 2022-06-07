package com.project.chess.service;

import com.project.chess.model.Color;
import com.project.chess.model.entity.Figure;
import com.project.chess.model.entity.User;

import java.util.List;

public interface UserService {
    User findOrCreatePlayer(String name, Long userId);

    String createNewPlayer(String name);

    void createInitialSetOfFigures(User user, Color color);

    void updatePlayers(User player1, User player2);
}
