package dk.dtu.compute.se.pisd.roborally.exceptions;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

/**
 * This class extends the Exception class and is used to throw an catch exceptions in situations where we are unable to move the the given Space
 * In that we throw this exception to state that this move is impossible.
 */
public class ImpossibleMoveException extends Exception {
    private String message;
    private Player player;
    private Space space;
    private Heading heading;

    public ImpossibleMoveException (Player player, Space space, Heading heading) {
        super("Move impossible");
        this.player = player;
        this.space = space;
        this.heading = heading;

    }
}
