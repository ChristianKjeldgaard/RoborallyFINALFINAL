/*
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
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;

import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * Space, Felter og vægge i spillet,
 * og udgør en vigtige rolle i forhold til spillets regler.
 */
public class Space extends Subject {

    public final Board board;
    private List<Heading> walls;
    private List<FieldAction> fieldActions;
    public final int x;
    public final int y;

    private Player player;

    public Space(Board board, int x, int y) {
        this.board = board;
        this.x = x;
        this.y = y;
        player = null;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        Player oldPlayer = this.player;
        if (player != oldPlayer &&
                (player == null || board == player.board)) {
            this.player = player;
            if (oldPlayer != null) {
                // this should actually not happen
                oldPlayer.setSpace(null);
            }
            if (player != null) {
                player.setSpace(this);
            }
            notifyChange();
        }
    }

    void playerChanged() {
        // This is a minor hack; since some views that are registered with the space
        // also need to update when some player attributes change, the player can
        // notify the space of these changes by calling this method.
        notifyChange();
    }

    /**
     * @author Anisa Riaz (s216237)
     * */
    public void setWalls(List<Heading> walls) {
        this.walls = walls;
    }

    /**
     * @Ali Shanoof (S215716)
     * Koden returnerer en liste over alle væggene i rummet.
     * */
    public List<Heading> getWalls() {
        return walls;
    }

    /**
     * @author Ali Hassan Tariq (s205853)
     * Koden er en ændring af den originale kode.
     * */
    public void setFieldActions(List<FieldAction> fieldActions) {
        this.fieldActions = fieldActions;
    }

    /**
     * @author Ali Hassan Tariq (s205853)
     * Koden bruges til at få en liste over alle de handlinger, der er udført på field.
     * */
    public List<FieldAction> getActions() {
        return fieldActions;


    }


}
