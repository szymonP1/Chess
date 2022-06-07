package com.project.chess.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoContentException extends RuntimeException{

    public NoContentException(String message) {
        super(message);
    }

}
