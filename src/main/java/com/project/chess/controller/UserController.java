package com.project.chess.controller;

import com.project.chess.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.project.chess.controller.url.UserMappings.CREATE_USER;
import static com.project.chess.controller.url.UserMappings.USER_API;

@RestController
@RequestMapping(value = USER_API)
public class UserController {

    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping(CREATE_USER)
    public ResponseEntity<?> createUser(
            @RequestParam(name = "name") String name) {
        try {
            return ResponseEntity.ok(userService.createNewPlayer(name));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
