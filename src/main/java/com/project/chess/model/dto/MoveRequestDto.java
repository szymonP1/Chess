package com.project.chess.model.dto;

public class MoveRequestDto {
    private String initPosition;
    private String destPosition;

    public String getInitPosition() {
        return initPosition;
    }

    public void setInitPosition(String initPosition) {
        this.initPosition = initPosition;
    }

    public String getDestPosition() {
        return destPosition;
    }

    public void setDestPosition(String destPosition) {
        this.destPosition = destPosition;
    }
}
