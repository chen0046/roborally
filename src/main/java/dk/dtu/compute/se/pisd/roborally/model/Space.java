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
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @coauthor Oliver Lyngholm Fiedler
 * @coauthor Andreas Vilholm Vilstrup
 * @coauthor Isabel Grimmig Jacobsen
 * @coauthor Alexander Solomon
 * @coauthor Chenxi Cai
 * @coauthor Ahmmad Shereef
 *
 * We decided to give Space an extra class in its attributes to make it easy for us to attach a wall to a space.
 * Spaces now have list of walls, so each space can have several walls.
 */
public class Space extends Subject {

    public final Board board;

    public final int x;
    public final int y;

    private Player player;
    public List<Heading> walls = new ArrayList<>();
    private int checkpoint;
    private int start;
    public Heading isConveyor;
    private int hole;
    private int rotateRight;
    private int rotateLeft;


    public Space(Board board, int x, int y) {
        this.board = board;
        this.x = x;
        this.y = y;
        player = null;
        checkpoint = -1;
        hole = -1;
        rotateRight = -1;
        rotateLeft = -1;
        start = -1;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        Player oldPlayer = this.player;
        if (player != oldPlayer &&
                (player == null || board == player.board)) {
            this.player = player;
            if (oldPlayer != null) {
                // this should actually not happen
                oldPlayer.setSpace(null);
            }
            if (player != null) {
                player.setSpace(this);
            }
            notifyChange();
        }
    }


    void playerChanged() {
        // This is a minor hack; since some views that are registered with the space
        // also need to update when some player attributes change, the player can
        // notify the space of these changes by calling this method.
        notifyChange();
    }

    public List<Heading> getWalls() {
        return walls;
    }

    /**
     * @return
     * We utilized a getter and setter function to be able to check for a checkpoint and create one as well
     * and many other things.
     */

    public int getCheckpoint() {
        return checkpoint;
    }

    public void setCheckpoint(int priority) {
        this.checkpoint = priority;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getHole() {
        return hole;
    }

    public void setHole(int hole) {
        this.hole = hole;

    }
    public int getRotateRight() {
        return rotateRight;
    }

    public void setRotateRight(int rotateRight) {
        this.rotateRight = rotateRight;
    }



    public int getRotateLeft() {
        return rotateLeft;
    }

    public void setRotateLeft(int rotateLeft) {
        this.rotateLeft = rotateLeft;
    }

}
