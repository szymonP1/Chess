package com.project.chess.model.entity.figures;

import com.project.chess.model.entity.Figure;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("1")
public class Horse extends Figure {

    public String getName() {
        return "HORSE";
    }
}
