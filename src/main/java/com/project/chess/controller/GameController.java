package com.project.chess.controller;

import com.project.chess.model.dto.MoveRequestDto;
import com.project.chess.service.FigureService;
import com.project.chess.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.project.chess.controller.url.GameMappings.*;

@RestController
@RequestMapping(value = GAME_API)
public class GameController {

    private final GameService gameService;
    private final FigureService figureService;


    public GameController(final GameService gameService,
                          final FigureService figureService) {
        this.gameService = gameService;
        this.figureService = figureService;
    }

    @GetMapping(value = START_GAME)
    public ResponseEntity<?> startGame(
            @RequestParam(name= "gameId") Long gameId,
            @RequestParam(name= "player1") Long player1Id,
            @RequestParam(name= "player2") Long player2Id){
        try {
            return ResponseEntity.ok(gameService.initializeGame(gameId, player1Id, player2Id).toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    @GetMapping(CHECK_STATUS)
//    public ResponseEntity<?> checkStatus(
//            @RequestParam(name = "gameId") Long gameId){
//
//    }

    @PostMapping(MAKE_MOVE)
    public ResponseEntity<?> makeMove(
            @RequestParam(name = "gameId") Long gameId,
            @RequestBody()MoveRequestDto moveRequestDto) {
        try {
            return ResponseEntity.ok(gameService.makeMove(gameId, moveRequestDto).toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(CHECK_MOVE)
    public ResponseEntity<?> checkMove(
            @RequestParam(name = "gameId") Long gameId,
            @RequestBody()MoveRequestDto moveRequestDto) {
        try {
            return ResponseEntity.ok(gameService.getMoves(gameId, moveRequestDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
