package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;
/**
 * @author Christian Kjeldgaard Jensen (s215703)
 * @author Seier Kieldsen (s215708)
 * */
public class Checkpoint extends FieldAction{

    private int number;


    public Checkpoint(int number){
        this.number = number;
    }

    /**
     * Checkpoints implementeres til spillet, hvor der tilføjes Checkpoints.
     * @param gameController the gameController of the respective game
     * @param space the space this action should be executed for
     * @return
     */
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
