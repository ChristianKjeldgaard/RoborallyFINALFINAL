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
 * @ Ali Shanoof (S215716)
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
