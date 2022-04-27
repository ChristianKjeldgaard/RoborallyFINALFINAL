package dk.dtu.compute.se.pisd.roborally.model;

import org.jetbrains.annotations.NotNull;

public class WinnerFoundException extends Exception{
    private Player winner;
    public WinnerFoundException(@NotNull Player winner){
        super("Winner found");
        this.winner = winner;
    }

    public Player getWinner() {
        return winner;
    }
}
