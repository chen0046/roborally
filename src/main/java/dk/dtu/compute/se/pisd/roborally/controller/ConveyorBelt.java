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
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.exceptions.ImpossibleMoveException;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class ConveyorBelt extends FieldAction {

    int length;
    public int x;
    public int y;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    private Heading heading;

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    public ConveyorBelt(int length, Heading heading, int x, int y) {
        setLength(length);
        setHeading(heading);
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        Space prevSpace = gameController.board.getNeighbour(space, heading.next().next());
        Space nextSpace = gameController.board.getNeighbour(space, heading);
        if(space.getPlayer() != null) {
            try {
                gameController.movePlayerToSpace(nextSpace, space.getPlayer(), heading);
            }
            catch (ImpossibleMoveException e) {
                e.printStackTrace();
            }
        }
        if (prevSpace.isConveyor != null) {
            doAction(gameController, prevSpace);
        }
        return true;
        // TODO needs to be implemented
    }

}
