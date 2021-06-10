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
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.INITIALISATION;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Board extends Subject {

    public final int width;

    public final int height;

    private Integer gameId;

    private final Space[][] spaces;

    private final List<Player> players = new ArrayList<>();

    private Player current;

    private Phase phase = INITIALISATION;

    private int step = 0;

    private boolean stepMode;

    public String boardName;

    int boardID;

    public int numberOfChecks;

    List<ConveyorBelt> conveyorBelts = new ArrayList<>();

    public Board(int width, int height, String boardName, int id) {
       // this.boardName = boardName;
        this.width = width;
        this.height = height;
        this.boardID = id;
        spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;
            }
        }
        //spilleplade 2 8*8
        if (boardID == 2) {
            spaces[0][2].setRotateLeft(1);
            spaces[7][5].setRotateRight(1);

            spaces[2][3].setHole(1);
            spaces[4][5].setHole(1);
            spaces[3][7].setHole(1);

            spaces[6][2].setCheckpoint(1);
            spaces[0][7].setCheckpoint(2);
            spaces[6][6].setCheckpoint(3);
            spaces[7][0].setCheckpoint(4);

            spaces[6][1].walls.add(Heading.WEST);
            spaces[3][2].walls.add(Heading.SOUTH);
            spaces[7][3].walls.add(Heading.SOUTH);
            spaces[0][6].walls.add(Heading.SOUTH);
            spaces[6][7].walls.add(Heading.NORTH);
            spaces[6][0].walls.add(Heading.WEST);

            conveyorBelts.add(new ConveyorBelt(3, Heading.NORTH, 4, 2));
            conveyorBelts.add(new ConveyorBelt(2, Heading.EAST, 2, 6));
            conveyorBelts.add(new ConveyorBelt(3, Heading.WEST, 4, 7));
            this.stepMode = false;
            setConveyor();
        }

        if (boardID == 1) {
            spaces[1][2].setHole(1);
            spaces[9][3].setHole(1);
            spaces[1][8].setHole(1);

            spaces[5][2].setRotateLeft(1);
            spaces[2][9].setRotateRight(1);

            spaces[7][7].setCheckpoint(1);
            spaces[9][2].setCheckpoint(2);
            spaces[1][3].setCheckpoint(3);
            spaces[1][9].setCheckpoint(4);
            spaces[5][4].setCheckpoint(5);

            spaces[3][3].walls.add(Heading.WEST);

            conveyorBelts.add(new ConveyorBelt(3, Heading.NORTH, 1, 1));
            conveyorBelts.add(new ConveyorBelt(2, Heading.EAST, 8, 0));
            conveyorBelts.add(new ConveyorBelt(4, Heading.SOUTH, 4, 7));
            conveyorBelts.add(new ConveyorBelt(3, Heading.EAST, 7, 9));
            setConveyor();
        }
    }

    public Board(int width, int height, int width1, int height1, String boardName, Space[][] spaces) {

        this.width = width1;
        this.height = height1;
        this.spaces = spaces;
    }

    public Board(int width, int height, int id) {
        this(width, height, "defaultboard",id);
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }

    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }

    public int getPlayersNumber() {
        return players.size();
    }

    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }

    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }

    public Player getCurrentPlayer() {
        return current;
    }

    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            notifyChange();
        }
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    public boolean isStepMode() {
        return stepMode;
    }

    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only, if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces); otherwise,
     * null will be returned.
     *
     * @param space   the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH:
                y = (y + 1) % height;
                break;
            case WEST:
                x = (x + width - 1) % width;
                break;
            case NORTH:
                y = (y + height - 1) % height;
                break;
            case EAST:
                x = (x + 1) % width;
                break;
        }

        return getSpace(x, y);
    }
    // this is actually a view aspect, but for making assignment V1 easy for
    // the students, this method gives a string representation of the current
    // status of the game

    // XXX: V2 changed the status so that it shows the phase, the player and the step


    public String getStatusMessage() {
        return "Player = " + getCurrentPlayer().getName() + ", Checkpoints reached: " + getCurrentPlayer().checkpointsReached + ", number of moves: " + getCount() + ", phase: " + getPhase().name() +
                ", Step: " + getStep();
    }


    // counts the number of moves
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        if (this.count != count) {
            this.count = count;
            notifyChange();
        }
    }

    public List<ConveyorBelt> getConveyorBelts() {
        return conveyorBelts;
    }

    public void setConveyor() {
        for (int i = 0; i < conveyorBelts.size(); i++) {
            ConveyorBelt conveyorBelt = conveyorBelts.get(i);
            Space currentSpace = spaces[conveyorBelt.x][conveyorBelt.y];
            for (int j = 0; j < conveyorBelt.getLength(); j++) {
                currentSpace.isConveyor = conveyorBelt.getHeading();
                currentSpace = getNeighbour(currentSpace, conveyorBelt.getHeading().next().next());
            }
        }
    }

    public int getBoardID() {
        return boardID;
    }

    public void setBoardID(int boardID) {
        this.boardID = boardID;
        if (boardID == 1) {
            numberOfChecks = 4;

        }

    }

}
