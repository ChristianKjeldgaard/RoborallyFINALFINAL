package dk.dtu.compute.se.pisd.roborally.controller;/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.ImpossibleMoveException;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class ConveyorBelt extends FieldAction {

    private Heading heading;

    public Heading getHeading() {
        return heading;
    }

    public ConveyorBelt(Heading heading){
        this.heading = heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }
    /**
     * @author Ali Hassan Tariq (s205853)
     * Koden er et kodestykke fra Game Controller-klassen. Metoden doAction() bruges til at flytte spilleren i spilcontrolleren. 
     * Den tjekker, om der er nogen naboer til denne spiller, ved at kalde getNeighbour(), og hvis der ikke er nogen, sker der ikke noget.
     * */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        Space neighbour = space.board.getNeighbour(space, heading);
        Player player = space.getPlayer();
        if(player != null && neighbour != null){
            try {
                player.setHeading(heading);
                gameController.moveToSpace(player, neighbour, heading);
                return true;
            } catch (ImpossibleMoveException e) {
                return false;
            }
        }
        return false;
    }
}
