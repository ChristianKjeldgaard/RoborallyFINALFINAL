package dk.dtu.compute.se.pisd.roborally.model;

import org.jetbrains.annotations.NotNull;

/**Ovenstående kode er en klasse, der udvider Exception-klassen.
 * Den indeholder en privat variabel, vinder, som gemmer spilleren, der vandt.
 * Konstruktøren af denne klasse opsætter undtagelsen med en streng, der siger "Vinder fundet".
 */
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
