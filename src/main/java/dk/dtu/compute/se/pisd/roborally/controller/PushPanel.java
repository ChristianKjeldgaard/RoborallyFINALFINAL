package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.ImpossibleMoveException;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ali Hassan Tariq (s205853)
 * @author Anisa Riaz (s216237)
 * @author Ali Shanoof (S215716)
 *
 * Koden er en del af PushPanel-klassen.
 * Koden indstiller variablerne pushDirection og executeNumbers til deres respektive værdier.
 * Metoden getPushDirection() returnerer værdien, som derefter bruges i andre metoder i denne klasse.
 * Det returnerer også de numre, der udføres, når dette panel trykkes.
 * */

public class PushPanel extends FieldAction{
    private Heading pushDirection;
    private ArrayList<Integer> executeNumbers;

    public void setExecuteNumbers(ArrayList<Integer> executeNumbers) {
        this.executeNumbers = executeNumbers;
    }

    public void setPushDirection(Heading pushDirection) {
        this.pushDirection = pushDirection;
    }

    public Heading getPushDirection() {
        return pushDirection;
    }

    public ArrayList<Integer> getExecuteNumbers() {
        return executeNumbers;
    }

    /**
     * Koden forsøger at flytte spilleren fra deres nuværende plads til en nabo.
     * Hvis de ikke er i den position, vil det returnere falsk
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        Space neighbour = space.board.getNeighbour(space, pushDirection);
        Player player = space.getPlayer();
        if(executeNumbers.contains(gameController.board.getStep() + 1)) {
            if (player != null && neighbour != null) {
                try {
                    gameController.moveToSpace(player, neighbour, pushDirection);
                    return true;
                } catch (ImpossibleMoveException e) {
                    return false;
                }
            }
        }
        return false;
    }
}
