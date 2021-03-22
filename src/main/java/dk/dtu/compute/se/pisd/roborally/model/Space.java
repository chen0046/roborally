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

import java.awt.*;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 *
 * We decided to give Space an extra class in its attributes to make it easy for us to attach a wall to a space.
 * For now every space can only have 1 wall though.
 */
public class Space extends Subject {

    public final Board board;

    public final int x;
    public final int y;

    private Player player;
    private Wall wall;

    public Space(Board board, int x, int y) {
        this.board = board;
        this.x = x;
        this.y = y;
        player = null;
        wall = null;
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

    /**
     * @return
     * We utilized a getter and setter function to be able to check for a wall and create one as well.
     */
    public Wall getWall() {
        return wall;
    }

    public void setWall(Heading heading) {
        this.wall = new Wall(heading);

    }
}
