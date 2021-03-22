package dk.dtu.compute.se.pisd.roborally.model;

import org.jetbrains.annotations.NotNull;

/**
 * We created a now class, making us able to give every single Space a Wall if that is what we want, by giving a wall a heading we can also easily find out how it should be placed
 * when saving the game and reloading it.
 */
public class Wall {
    public Heading heading;
    public Wall(@NotNull Heading heading) {
        this.heading = heading;
    }
}
