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

import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.exceptions.ImpossibleMoveException;
import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.ACTIVATION;

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
 *
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void movePlayerToSpace(@NotNull Space space, @NotNull Player player, @NotNull Heading heading) throws ImpossibleMoveException {
        if(winnerCheck(space,player)){
            System.out.println(player.getName() + " tillykke, du har vundet!!!");
        }
        Player other = space.getPlayer();
        if (wallCheck(space, player, heading)) {
            if (other != null) {
                Space target = board.getNeighbour(space, heading);
                if (target != null) {
                    movePlayerToSpace(target, other, heading);
                } else {
                    throw new ImpossibleMoveException(player, space, heading);
                }
            }
            player.setSpace(space);
            int playerNumber = board.getPlayerNumber(player);
            Player nextPlayer = board.getPlayer((playerNumber + 1) % board.getPlayersNumber());
            board.setCurrentPlayer(nextPlayer);
            board.setCount(board.getCount() + 1);
        } else {
            throw new ImpossibleMoveException(player, space, heading);
        }
    }

    // XXX: V2

    /**
     * When this method is called upon, the programming phase initiates,
     * and it lets you put programming cards in your programming field.
     */
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    // XXX: V2
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    // XXX: V2
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    // XXX: V2
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX: V2
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    // XXX: V2
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    // XXX: V2
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    // XXX: V2
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == ACTIVATION && !board.isStepMode());
    }

    // XXX: V2
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    if (command.isInteractive()) {
                        board.setPhase(Phase.PLAYER_INTERACTION);
                        return;
                    }
                    executeCommand(currentPlayer, command);
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    // XXX: V2
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                case FASTER_FORWARD:
                    this.fasterForward(player);
                    break;
                case MOVE_BACK:
                    this.moveBack(player);
                    break;
                case U_TURN:
                    this.uTurn(player);
                    break;
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    /**
     * Here we have all the different programming card functionalities, for the cards we have made so far.
     */
    public void moveForward(@NotNull Player player) {
        if (player.board == board) {
            Heading heading = player.getHeading();
            Space space = player.getSpace();
            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                try {
                    movePlayerToSpace(target, player, heading);
                } catch (ImpossibleMoveException e) {
                    System.out.println("Impossible move");
                    // We do nothing here for now
                }
            }
        }
    }

    public void fastForward(@NotNull Player player) {
        if (player.board == board) {
            Space current = player.getSpace();
            Heading heading = player.getHeading();
            Space neighbour = board.getNeighbour(current, heading);
            Space target = board.getNeighbour(neighbour, heading);
            if (target != null) {
                try {
                    movePlayerToSpace(neighbour, player, heading);
                }
                catch (ImpossibleMoveException e) {
                    // Nothing for now
                }
                try {
                    movePlayerToSpace(target, player, heading);
                }
                catch (ImpossibleMoveException e) {
                    player.setSpace(neighbour);
                    //Do nothing for now
                }
            }
        }
    }
    public void fasterForward(@NotNull Player player) {
        if (player.board == board) {
            Space current = player.getSpace();
            Heading heading = player.getHeading();
            Space neighbour = board.getNeighbour(current, heading);
            Space neighboursNeighbour = board.getNeighbour(neighbour, heading);
            Space target = board.getNeighbour(neighboursNeighbour, heading);
            if (target != null) {
                try {
                    movePlayerToSpace(neighbour, player, heading);
                }
                catch (ImpossibleMoveException e) {
                    // Nothing for now
                }
                try {
                    movePlayerToSpace(neighbour, player, heading);
                }
                catch (ImpossibleMoveException e) {
                    player.setSpace(neighbour);
                    // Nothing for now
                }
                try {
                    movePlayerToSpace(target, player, heading);
                }
                catch (ImpossibleMoveException e) {
                    player.setSpace(neighboursNeighbour);
                    //Do nothing for now
                }
            }
        }
    }
    public void moveBack(@NotNull Player player) {
        Heading heading = player.getHeading().next().next();
        Space space = player.getSpace();
        Space target = board.getNeighbour(space, heading);
        if (target != null) {
            try {
                movePlayerToSpace(target, player, heading);
            } catch (ImpossibleMoveException e) {
                // We do nothing here for now
            }
        }
    }
    public void turnRight(@NotNull Player player) {
        Space current = player.getSpace();
        if (current != null && player.board == current.board) {
            player.setHeading(player.getHeading().next());
        }
    }

    public void turnLeft(@NotNull Player player) {
        Space current = player.getSpace();
        if (current != null && player.board == current.board) {
            player.setHeading(player.getHeading().prev());
        }
    }
    public void uTurn(@NotNull Player player) {
        Space current = player.getSpace();
        if (current != null && player.board == current.board) {
            player.setHeading(player.getHeading().next().next());
        }
    }

    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method and the one underneath is made for a programming card that requires a player interaction
     */
    public void turnLeftOrRight(@NotNull Player player, Boolean choice) {
        if (choice) {
            executeCommandAndContinue(Command.RIGHT, player);
        }
        else {
            executeCommandAndContinue(Command.LEFT, player);
        }
    }

    public void executeCommandAndContinue(Command option, Player player) {
        int step = board.getStep();
        board.setPhase(ACTIVATION);
        executeCommand(player, option);
        int nextPlayerNumber = board.getPlayerNumber(player) + 1;
        if (nextPlayerNumber < board.getPlayersNumber()) {
            board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
        } else {
            step++;
            if (step < Player.NO_REGISTERS) {
                makeProgramFieldsVisible(step);
                board.setStep(step);
                board.setCurrentPlayer(board.getPlayer(0));
            } else {
                startProgrammingPhase();
            }
        }
        if (board.getPhase() == Phase.ACTIVATION && !board.isStepMode()) {
            continuePrograms();
        }
    }

    /**
     * This method checks if a field has a wall and if the wall is going to collide based on the heading of the wall.
     * @param space The space to which the current player is and the space to which the wall is.
     * @param player The player whose turn it currently is
     * @param heading The heading of the player
     * @return Since it's a boolean it should return a true or a false depending on the outcome of the method
     */
    public boolean wallCheck(Space space, Player player, Heading heading) {
        Wall firstWall = player.getSpace().getWall();
        Wall secondWall = space.getWall();
        if (firstWall != null) {
            if (firstWall.heading == heading) {
                return false;
            }
        } if (secondWall != null) {
            if (secondWall.heading.next().next() == heading) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method checks if a player has hit the final checkpoint (so far in our prototype we only have 1 checkpoint), and announces a winner.
     * @param space The space the current player in.
     * @param player The player whose turn it currently is (not used yet)
     * @return Since it's a boolean it should return a true or a false depending on the outcome of the method
     */
    public boolean winnerCheck(Space space, Player player) {
        Checkpoint checkpointField = space.getCheckpoint();
        if (checkpointField != null) {
                return true;
        }
        return false;
    }
}