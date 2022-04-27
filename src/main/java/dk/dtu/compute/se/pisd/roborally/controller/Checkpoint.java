package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;
/**
 * @author Seier
 * @author Christian
 * */
public class Checkpoint extends FieldAction{

    private int number;

    public Checkpoint(int number){
        this.number = number;
    }

    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space){
        Player player = space.getPlayer();
        if(player.getCheckpointsHit() == number - 1){
            player.addCheckpoint();
            return true;
        }
        return false;
    }

    public int getNumber() {
        return number;
    }
}
