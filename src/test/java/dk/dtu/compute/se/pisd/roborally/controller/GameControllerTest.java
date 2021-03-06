package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.exceptions.ImpossibleMoveException;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT,1);
        gameController = new GameController(board);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, null,"Player " + i);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }

    @Test
    void moveCurrentPlayerToSpace() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);

        try {
            gameController.movePlayerToSpace(board.getSpace(0, 4), player1, player1.getHeading());
        } catch (ImpossibleMoveException e) {
            e.printStackTrace();
        }

        Assertions.assertEquals(player1, board.getSpace(0, 4).getPlayer(), "Player " + player1.getName() + " should beSpace (0,4)!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
        Assertions.assertEquals(player2, board.getCurrentPlayer(), "Current player should be " + player2.getName() +"!");
    }

    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveForward(current);

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    @Test
    void turnLeft() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        Heading original = current.getHeading();
        Heading turn = null;
        if (original == Heading.NORTH) {
            turn = Heading.WEST;
        }
        else if (original == Heading.EAST) {
            turn = Heading.NORTH;
        }
        else if (original == Heading.SOUTH) {
            turn = Heading.EAST;
        }
        else if (original == Heading.WEST) {
            turn = Heading.SOUTH;
        }

        gameController.turnLeft(current);
        Assertions.assertEquals(turn, current.getHeading());
    }

    @Test
    void turnRight() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        Heading original = current.getHeading();
        Heading turn = null;
        if (original == Heading.NORTH) {
            turn = Heading.EAST;
        }
        else if (original == Heading.EAST) {
            turn = Heading.SOUTH;
        }
        else if (original == Heading.SOUTH) {
            turn = Heading.WEST;
        }
        else if (original == Heading.WEST) {
            turn = Heading.NORTH;
        }

        gameController.turnRight(current);
        Assertions.assertEquals(turn, current.getHeading());
    }

    @Test
    void fastFwd() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.fastForward(current);

        Assertions.assertEquals(current, board.getSpace(0, 2).getPlayer(), "Player " + current.getName() + " should beSpace (0,2)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
        Assertions.assertNull(board.getSpace(0,1).getPlayer(),"Space (0,1) should be empty");
    }
}