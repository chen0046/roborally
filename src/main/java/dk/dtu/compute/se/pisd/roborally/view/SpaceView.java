/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @coauthor Andreas Vilstrup, s205450@student.dtu.dk
 * @coauthor Oliver lyngholm Fiedler, s205423@student.dtu.dk
 * @coauthor Isabel Jacobsen
 * @coauthor Ahmad shereef
 * @coauthor Alexander Solomon
 * @coauthor Chenxi Cai
 */
public class SpaceView extends StackPane implements ViewObserver {
    final public static int SPACE_HEIGHT = 75; // 60; // 75;
    final public static int SPACE_WIDTH = 75;  // 60; // 75;

    public final Space space;


    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        if ((space.x + space.y) % 2 == 0) {
            this.setStyle("-fx-background-color: white;");
        } else {
            this.setStyle("-fx-background-color: black;");
        }

        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updatePlayer() {

        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0 );
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
                arrow.setStroke(Color.BLACK);
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90*player.getHeading().ordinal())%360);
            this.getChildren().add(arrow);
        }
    }

    /**
     * Here we added the new methods we created for the GUI in the right order of
     * which visual should be prioritized
     *
     */
    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            this.getChildren().clear();
            updateHole();
            updateWall();
            updateConveyor();
            updateRotateRight();
            updateRotateLeft();
            updateCheckpoint();
            updatePlayer();
        }
    }

    /**
     * This method is used when we know we need a wall but we need to find out which direction it is going to be placed
     */
    public void updateWall() {
        List walls = space.getWalls();
        if (walls != null) {
            for (int i = 0; i < walls.size(); i++) {
                if (walls.get(i) == Heading.NORTH) {
                    wallNorth();
                }
                if (walls.get(i) == Heading.EAST) {
                    wallEast();
                }
                if (walls.get(i) == Heading.WEST) {
                    wallWest();
                }
                if (walls.get(i) == Heading.SOUTH) {
                    wallSouth();
                }
            }
        }
    }

    /**
     * Here are 4 methods for each direction a wall can have, the only difference in the methods is the gc.strokeline
     * where we give the wall other coordinates.
     */
    public void wallSouth() {
        Canvas canvas = new Canvas(SPACE_WIDTH, SPACE_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.RED);
        gc.setLineWidth(5);
        gc.setLineCap(StrokeLineCap.ROUND);

        gc.strokeLine(2,SPACE_HEIGHT-2,SPACE_WIDTH-2,SPACE_HEIGHT-2);
        this.getChildren().add(canvas);
    }

    public void wallWest() {
        Canvas canvas = new Canvas(SPACE_WIDTH, SPACE_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.RED);
        gc.setLineWidth(5);
        gc.setLineCap(StrokeLineCap.ROUND);

        gc.strokeLine(2, SPACE_HEIGHT-72, 2 , SPACE_HEIGHT-2 );
        this.getChildren().add(canvas);
    }

    public void wallNorth() {
        Canvas canvas = new Canvas(SPACE_WIDTH, SPACE_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.RED);
        gc.setLineWidth(5);
        gc.setLineCap(StrokeLineCap.ROUND);

        gc.strokeLine(2, SPACE_HEIGHT-73, SPACE_WIDTH-2 , SPACE_HEIGHT-73 );
        this.getChildren().add(canvas);
    }
    public void wallEast() {
        Canvas canvas = new Canvas(SPACE_WIDTH, SPACE_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.RED);
        gc.setLineWidth(5);
        gc.setLineCap(StrokeLineCap.ROUND);

        gc.strokeLine(SPACE_WIDTH-2, SPACE_HEIGHT-2, SPACE_WIDTH-2 , SPACE_HEIGHT-73 );
        this.getChildren().add(canvas);
    }

    /**
     * Here we create the visual for the checkpoint and the startfields
     */
    public void checkpoint() {
        int priority = space.getCheckpoint();
        Canvas canvas = new Canvas(SPACE_WIDTH, SPACE_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.MAGENTA);
        gc.strokeText("CHECKPOINT\n\t" + priority,10,SPACE_HEIGHT-30,SPACE_WIDTH-20);
        this.getChildren().add(canvas);
    }
    public void startField() {
        Circle start = new Circle(35,Color.GREENYELLOW);
        Canvas canvas = new Canvas(SPACE_WIDTH, SPACE_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.strokeText(" START",20,SPACE_HEIGHT-60,SPACE_WIDTH-20);
        this.getChildren().add(start);
        this.getChildren().add(canvas);
    }
    public void updateCheckpoint() {
        int start = space.getStart();
        int checkpoint = space.getCheckpoint();
        if(start == 0) {
            startField();
        }
        else if(checkpoint != -1) {
            checkpoint();
        }
    }

    /**
     * Here is the design for the conveyorbelts
     */
    public void conveyorSouth() {
        Polygon moveSouth = new Polygon(10,35,25,60,40,35);
        moveSouth.setStroke(Color.VIOLET);
        this.getChildren().add(moveSouth);
    }
    public void conveyorNorth() {
        Polygon moveNorth = new Polygon(10,35,25,60,40,35);
        moveNorth.setStroke(Color.VIOLET);
        moveNorth.setRotate(180);
        this.getChildren().add(moveNorth);
    }
    public void conveyorEast() {
        Polygon moveEast = new Polygon(10,35,25,60,40,35);
        moveEast.setStroke(Color.VIOLET);
        moveEast.setRotate(270);
        this.getChildren().add(moveEast);
    }
    public void conveyorWest() {
        Polygon moveWest = new Polygon(10,35,25,60,40,35);
        moveWest.setStroke(Color.VIOLET);
        moveWest.setRotate(90);
        this.getChildren().add(moveWest);
    }
    public void updateConveyor() {
        if (space.isConveyor == Heading.NORTH) {
            conveyorNorth();
        }
        if (space.isConveyor == Heading.EAST) {
            conveyorEast();
        }
        if (space.isConveyor == Heading.WEST) {
            conveyorWest();
        }
        if (space.isConveyor == Heading.SOUTH) {
            conveyorSouth();
        }
    }

    /**
     * the design for the hole and the rotate fields
     */
    public void hole() {
        Circle redHole = new Circle(30,Color.DARKRED);
        this.getChildren().add(redHole);
    }

    public void updateHole() {
        int placeHole = space.getHole();
        if(placeHole == 1) {
            hole();
        }
    }

    public void rotateLeft() {
        Canvas canvas = new Canvas(SPACE_WIDTH, SPACE_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLUE);
        gc.strokeText("GearLeft\n\t", 10, SPACE_HEIGHT - 10, SPACE_WIDTH - 20);

        this.getChildren().add(canvas);
    }

    public void rotateRight() {
        Canvas canvas = new Canvas(SPACE_WIDTH, SPACE_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLUE);
        gc.strokeText("GearRight\n\t", 10, SPACE_HEIGHT - 10, SPACE_WIDTH - 20);

        this.getChildren().add(canvas);
    }

    public void updateRotateLeft() {
        int placeRotateLeft = space.getRotateLeft();
        if (placeRotateLeft == 1) {
            rotateLeft();
        }
    }

    public void updateRotateRight() {
        int placeRotateRight = space.getRotateRight();
        if (placeRotateRight == 1) {
            rotateRight();
        }
    }
}


