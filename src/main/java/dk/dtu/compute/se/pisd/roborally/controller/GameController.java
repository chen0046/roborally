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
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Optional;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.ACTIVATION;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @coauthor Oliver lyngholm Fiedler, s205423@student.dtu.dk
 * @coauthor Andreas Vilstrup, s205450@student.dtu.dk
 * @coauthor Isabel Jacobsen
 * @coauthor Alexander Solomon
 * @coauthor Chenxi Cai
 * @coauthor Ahmad shereef
 */
public class GameController {

    final public Board board;
    public ArrayList<Player> holePlayer = new ArrayList();
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
        Player other = space.getPlayer();
        if (wallCheck(space, player, heading)) {
            if (other != null) {
                Space target = board.getNeighbour(space, heading);
                if (!wallCheck(board.getNeighbour(target,heading), other , heading)) {
                    throw new ImpossibleMoveException(player, space, heading);
                } else if (target != null) {
                    movePlayerToSpace(target, other, heading);
                }
            }
        } else {
            throw new ImpossibleMoveException(player, space, heading);
        }
        player.setSpace(space);
        nextPlayerTurn(player);

    }

    public void holeCheck() {
        if (holePlayer.size() > 0) {
            while (holePlayer.size() > 0) {
                Player currentPlayer = holePlayer.get(0);
                if (currentPlayer.checkpointsReached == 0) {
                    Space start = board.getSpace(currentPlayer.getPlayerID(), 0);
                    currentPlayer.setSpace(start);
                } else {
                    Space lastCheckpoint = currentPlayer.getLastCheckpoint();
                    try {
                        movePlayerToSpace(lastCheckpoint, currentPlayer, Heading.SOUTH);
                    }
                    catch (ImpossibleMoveException e) {
                        e.printStackTrace();
                    }
                }
                holePlayer.remove(0);
            }
        }
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            Space currentSpace = player.getSpace();
            if (currentSpace != null) {
                if (currentSpace.getHole() == 1) {
                    holePlayer.add(player);
                    currentSpace.setPlayer(null);
                }
            }
        }
    }

    public void winCheck(Player player) {
        if (player.checkpointsReached == board.numberOfChecks) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("TILLYKKE!!!");
            alert.setContentText(player.getName() + " tillykke, du har vundet!\nTryk OK for at afslutte programmet\nTryk Cancel og dernæst på stop game og new game i højre\n hjørne for at starte nyt spil");
            Optional<ButtonType> resultChoice = alert.showAndWait();
            if (!resultChoice.isPresent() || resultChoice.get() == ButtonType.CANCEL) {
                return;
            }
            else if (resultChoice.get() == ButtonType.OK) {
                Platform.exit();
            }
        }
    }

    public void nextPlayerTurn(Player player) {
        int playerNumber = board.getPlayerNumber(player);
        Player nextPlayer = board.getPlayer((playerNumber + 1) % board.getPlayersNumber());
        if (nextPlayer.getSpace() == null) {
            nextPlayerTurn(nextPlayer);
        }
        board.setCurrentPlayer(nextPlayer);
        board.setCount(board.getCount() + 1);
    }

    public void checkCheckpoints(@NotNull Space space, Player player) {
        int playerCheckpoint = player.checkpointsReached;
        if (space.getCheckpoint() != -1) {
            if (space.getCheckpoint() - 1 == playerCheckpoint) {
                player.setCheckpointsReached(playerCheckpoint + 1);
                System.out.println(space.getPlayer().getName() + ", du har nu ramt checkpoint " + space.getCheckpoint());
                player.setLastCheckpoint(space);
            } else if (space.getCheckpoint() <= playerCheckpoint) {
                System.out.println(space.getPlayer().getName() + ", du har allerede været forbi dette checkpoint");
            } else {
                System.out.println(space.getPlayer().getName() + ", du er ikke nået til dette checkpoint endnu");
            }
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
                    }  else {
                        for (int i = 0; i < board.getPlayersNumber(); i++) {
                            if (board.getPlayer(i).getSpace() != null) {
                                Space space = board.getPlayer(i).getSpace();
                                checkCheckpoints(space, board.getPlayer(i));
                            }


                        }
                            for (int i =0; i < board.getPlayersNumber(); i++){
                                Space space = board.getPlayer(i).getSpace();
                                rotateGearLeft(board.getPlayer(i), space);
                                rotateGearRight(board.getPlayer(i), space);
                        }
                        for (int i = 0; i < board.getPlayersNumber(); i++) {
                            winCheck(board.getPlayer(i));
                        }
                        holeCheck();
                        moveOnConveyor();
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
        if (player.board == board && command != null) {
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
        if (player.getSpace() != null) {
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
    }

    public void fastForward(@NotNull Player player) {
        if (player.board == board && player.getSpace() != null) {
            Heading heading = player.getHeading();
            for (int i = 0; i < 2; i++) {
                Space current = player.getSpace();
                Space neighbor = board.getNeighbour(current, heading);
                try {
                    movePlayerToSpace(neighbor, player, heading);
                } catch (ImpossibleMoveException e) {
                    System.out.println("Impossible move");
                    break;
                }
            }
        }
    }
    public void fasterForward(@NotNull Player player) {
        if (player.board == board && player.getSpace() != null) {
            Heading heading = player.getHeading();
                for (int i = 0; i < 3; i++) {
                    Space current = player.getSpace();
                    Space neighbor = board.getNeighbour(current, heading);
                    try {
                        movePlayerToSpace(neighbor, player, heading);
                    } catch (ImpossibleMoveException e) {
                        System.out.println("Impossible move");
                        break;
                    }
                }
        }
    }
    public void moveBack(@NotNull Player player) {
        if (player.getSpace() != null) {
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
            executeCommandAndContinue(Command.LEFT, player);
        }
        else {
            executeCommandAndContinue(Command.RIGHT, player);
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
        Space firstSpace = player.getSpace();
        if (firstSpace != null) {
            for (int i = 0; i < firstSpace.walls.size(); i++) {
                if (firstSpace.walls.get(i) == heading) {
                    return false;
                }
            }
        }
        for (int i = 0; i < space.walls.size(); i++) {
            if (space.walls.get(i) == heading.next().next()) {
                return false;
            }
        }
        return true;
    }


    public void moveOnConveyor() {
        for (int i = 0; i < board.getConveyorBelts().size(); i++) {
            ConveyorBelt conveyorBelt = board.getConveyorBelts().get(i);
            Space space = board.getSpace(conveyorBelt.x, conveyorBelt.y);
            conveyorBelt.doAction(this, space);
        }
    }

    public void rotateGearLeft(Player player, Space space) {
        Space current = player.getSpace();
        if (current != null) {
            if (space.getRotateLeft() == 1) {
                turnLeft(current.getPlayer());
            }
        }
    }

    public void rotateGearRight(Player player, Space space) {
        Space current = player.getSpace();
        if (current != null) {
            if (space.getRotateLeft() == 1) {
                turnRight(current.getPlayer());
            }
        }
    }
}