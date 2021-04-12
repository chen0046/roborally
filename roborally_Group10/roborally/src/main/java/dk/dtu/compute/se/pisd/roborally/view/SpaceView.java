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
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import org.jetbrains.annotations.NotNull;

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
        this.getChildren().clear();

        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0 );
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90*player.getHeading().ordinal())%360);
            this.getChildren().add(arrow);
        }
    }

    /**
     * Here we added the new methods we created for the wall and the checkpoint
     *
     */
    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            updatePlayer();
            updateWall();
            updateCheckpoint();
        }
    }

    /**
     * This method is used when we know we need a wall but we need to find out which direction it is going to be placed
     */
    public void updateWall() {
        Wall wall = space.getWall();
        if (wall != null) {
            if (wall.heading == Heading.NORTH) {
                wallNorth();
            }
            if (wall.heading == Heading.EAST) {
                wallEast();
            }
            if (wall.heading == Heading.WEST) {
                wallWest();
            }
            if (wall.heading == Heading.SOUTH) {
                wallSouth();
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
     * Here we create the visual for the checkpoint (currently temporary design)
     */
    public void checkpoint() {
        Canvas canvas = new Canvas(SPACE_WIDTH, SPACE_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.MAGENTA);
        gc.strokeText("CHECKPOINT",10,SPACE_HEIGHT-10,SPACE_WIDTH-20);

        this.getChildren().add(canvas);
    }

    /**
     * This method checks if there is supposed to be a checkpoint and places it if necessary, by calling checkpoint().
     */
    public void updateCheckpoint() {
        Checkpoint checkpoint = space.getCheckpoint();
        if(checkpoint != null) {
            checkpoint();
        }
    }
}
