package dk.dtu.compute.se.pisd.roborally.model;

/**
 * @author Anisa Riaz (s216237)
 * */
public class ImpossibleMoveException extends Exception {
    private Player player;
    private Space space;
    private Heading heading;
    public ImpossibleMoveException(Player player,
                                   Space space,
                                   Heading heading) {
        super("Move impossible");
        this.player = player;
        this.space = space;
        this.heading = heading;
    }
}
