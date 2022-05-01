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
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * @ Ali H
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space)  {
        if(space.getPlayer() == null){
            space.setPlayer(board.getCurrentPlayer());
            //set current player to next player
            setNextPlayer();
            int incremented_counter = board.getCounter() + 1;
            board.setCounter(incremented_counter);
        }

    }
    /**
     * @ Ali H
     * */
    private void setNextPlayer(){
        Player currentPlayer = board.getCurrentPlayer();
        if(currentPlayer != null){
            if(board.getPlayer(board.getPlayersNumber()-1).equals(currentPlayer)){
                board.setCurrentPlayer(board.getPlayer(0));
            }else{
                int number = board.getPlayerNumber(currentPlayer);
                board.setCurrentPlayer(board.getPlayer(number+1));
            }
        }else {
            board.setCurrentPlayer(board.getPlayer(0));
        }
    }

    public void executeCommandOptionAndContinue(Command command){
        Player player = board.getCurrentPlayer();
        if(player != null && command != null){
            executeCommand(player, command);
            setNextPlayer();
            board.setPhase(Phase.ACTIVATION);
        }

        if(board.isStepMode() == false){
            continuePrograms();
        }
    }

    // XXX: V2
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    // XXX: V2
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    // XXX: V2
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    // XXX: V2
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX: V2
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    // XXX: V2
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    // XXX: V2
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    // XXX: V2
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    /**
     * @ Ali Malik
     * @author Ekkart Kindler
     * */
    // XXX: V2
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    if(command.isInteractive()){
                        board.setPhase(Phase.PLAYER_INTERACTION);
                        return;
                    }
                    executeCommand(currentPlayer, command);
                }

                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                        for(int i = 0; i < board.getPlayersNumber(); i++)
                            triggerBoardElements(board.getPlayer(i));
                    } else {
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    /**
     * @author Seier
     * @author Christian
     * Håndterer aktivering af boardelements
     */
    public void triggerBoardElements(Player currentPlayer){
        List<FieldAction> fieldActions = currentPlayer.getSpace().getActions();
        if(fieldActions != null){
            for (FieldAction action: fieldActions) {
                action.doAction(this, currentPlayer.getSpace());
            }
        }
    }
    /**
     * @ Anisa
     * Udbygget med ekstra commands
     * */
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                case BACK_UP:
                    this.goBackwards(player);
                    break;
                case U_TURN:
                    this.uTurn(player);
                    break;
                case MOVE_2:
                    this.moveTwo(player);
                    break;
                default:
                // DO NOTHING (for now)
            }
        }
    }
    /**
     * @Ali H
     * @author Ekkart Kindler
     * */
    public void moveToSpace(
            @NotNull Player player,
            @NotNull Space space,
            @NotNull Heading heading) throws ImpossibleMoveException {
        if (player != null && heading != null) {
            Space playerSpace = player.getSpace();
            if(playerSpace.getWalls() != null) {
                if (playerSpace.getWalls().contains(heading)) {
                    throw new ImpossibleMoveException(player, space, heading);
                }
            }
            if(space.getWalls() != null){
                if(space.getWalls().contains(heading.opposite())){
                    throw new ImpossibleMoveException(player,space,heading);
                }
            }
            Player other = space.getPlayer();

            if (other != null) {
                Space target = board.getNeighbour(space, heading);
                if (target != null) {
                    moveToSpace(other, target, heading);
                } else {
                    throw new ImpossibleMoveException(player, space, heading);
                }
            }
            player.setSpace(space);

        }
    }
    /**
     * @ Ali H
     * */
    public void moveForward(@NotNull Player player) {
        if(player != null && player.getHeading() != null){
            Space currentSpace = player.getSpace();
            Space nextSpace = board.getNeighbour(currentSpace, player.getHeading());
            if(nextSpace != null) {
                try {
                    moveToSpace(player, nextSpace, player.getHeading());
                }catch (ImpossibleMoveException e){

                }
            }
        }
    }

    /**
     * @Ali Malik
     * */
    public void fastForward(@NotNull Player player) {
        for (int i = 0; i < 3; i++) {
            if(player != null && player.getHeading() != null) {
                Space nextSpace = board.getNeighbour(player.getSpace(), player.getHeading());
                try {
                    moveToSpace(player, nextSpace, player.getHeading());
                }catch (ImpossibleMoveException e){

                }
            }else{
                return;
            }
        }
    }
    /**
     * @Christian 
     * */
    public void moveTwo(@NotNull Player player){
        for (int i = 0; i < 2; i++) {
            if(player != null && player.getHeading() != null) {
                Space nextSpace = board.getNeighbour(player.getSpace(), player.getHeading());
                try {
                    moveToSpace(player, nextSpace, player.getHeading());
                }catch (ImpossibleMoveException e){

                }
            }else{
                return;
            }
        }
    }
    /**
     * @Seier
     * */
    public void uTurn(@NotNull Player player){
        if(player != null && player.getHeading() != null){
            player.setHeading(player.getHeading().opposite());
        }
    }
    /**
     * @Anisa
     * */
    public void goBackwards(@NotNull Player player){
        if(player != null && player.getHeading() != null){
            player.setHeading(player.getHeading().opposite());
            Space currentSpace = player.getSpace();
            Space nextSpace = board.getNeighbour(currentSpace, player.getHeading());
            if(nextSpace != null) {
                moveCurrentPlayerToSpace(nextSpace);
            }
            player.setHeading(player.getHeading().opposite());
        }
    }
    // TODO Assignment V2
    public void turnRight(@NotNull Player player) {
        if(player != null && player.getHeading() != null){
            Heading nextHeading = player.getHeading().next();
            if(nextHeading != null) {
                player.setHeading(nextHeading);
            }
        }
    }
    /**
     * @Ali Malik
     * */
    // TODO Assignment V2
    public void turnLeft(@NotNull Player player) {
        if(player != null && player.getHeading() != null){
            Heading nextHeading = player.getHeading().prev();
            if(nextHeading != null) {
                player.setHeading(nextHeading);
            }
        }
    }

    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * A method called when no corresponding controller operation is implemented yet. This
     * should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

}
