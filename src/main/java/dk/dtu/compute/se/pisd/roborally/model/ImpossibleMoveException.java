package dk.dtu.compute.se.pisd.roborally.model;

/**
 * @author Anisa Riaz (s216237)
 * Koden er et eksempel på en klasse, der er skrevet for at hjælpe programmøren til at forstå,
 * hvordan man opretter og bruger denne nye type undtagelse.
 * Koden ovenfor opretter en ny forekomst af ImpossibleMoveException-klassen med en enkelt parameter, player.
 * Spillerparameteren sendes ind af den, der ringer (spillets motor).
 * Super() metodekaldet øverst i koden opsætter nogle grundlæggende oplysninger om,
 * hvilken slags undtagelse dette er, inklusive dens navn og beskrivelse.
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
