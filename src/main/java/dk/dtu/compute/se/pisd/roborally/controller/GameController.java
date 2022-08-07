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
     * @ Ali Shanoof (S215716)
     * Koden flytter den aktuelle afspiller til den angivne plads. 
     * Hvis der ikke er nogen spiller på det felt, sætter det den nye spiller til at være den næste på brættet. 
     * Den øger også en tæller med 1 og opdaterer den på spillepladen, hver gang en spiller bevæger sig.
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
     * @ Ali Shanoof (S215716)
     * Hvis der ikke er nogen aktuel spiller i spillet, 
     * sætter koden den næste spiller til 0.
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

    /**
     * Koden udføres, når spilleren trykker på "fortsæt"-knappen på spillepladen.
     * Hvis der ikke er nogen aktuel spiller, eller kommandoen ikke eksisterer,
     * vil setNextPlayer() blive kaldt for at finde ud af, hvem der skal være den næste i rækken til turen.
     * @param command
     */
    public void executeCommandOptionAndContinue(Command command){
        Player player = board.getCurrentPlayer();
        if(player != null && command != null){
            executeCommand(player, command);
            setNextPlayer();
            board.setPhase(Phase.ACTIVATION);
        }
        int nextPlayerNumber = board.getPlayerNumber(player) + 1;
        if (nextPlayerNumber < board.getPlayersNumber()) {
            board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
        } else {
            for(int i = 0; i < board.getPlayersNumber(); i++)
                triggerBoardElements(board.getPlayer(i));
            board.setStep(board.getStep()+1);
            if (board.getStep() < Player.NO_REGISTERS) {
                makeProgramFieldsVisible(board.getStep());
                board.setStep(board.getStep());
                board.setCurrentPlayer(board.getPlayer(0));
            } else {
                startProgrammingPhase();
            }
        }

        if(board.isStepMode() == false){
            continuePrograms();
        }
    }

    // XXX: V2

    /**
     * Kode forsøger at skabe et bræt med to spillere.
     * Den første spiller er sat som den første spiller på brættet.
     * Koden analyserer derefter alle kortene på brættet fra 1-8.
     * Det afsluttes med at generere et tilfældigt kommandokort og også gøre det synligt.
     */
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

    /**
     * Koden er en metode, der genererer et tilfældigt kommandokort.
     * ver gang denne funktion køres, vil den generere et nyt CommandCard-objekt med en tilfældigt valgt kommando fra listen over tilgængelige kort.
     * Den opretter en heltalsvariabel kaldet tilfældig og sætter dens værdi til at være lig med Math. random() * commands.length.
     * @return
     */
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    // XXX: V2

    /**
     * Koden gør alle felter i programmet usynlige og gør dem så synlige igen, når fasen skifter til AKTIVERING.
     * Koden er implementeringen af faseskiftmetoden til en computers onboarding-proces,
     * hvor onboarding udløses af den aktuelle spiller, der er ombord.
     */
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    // XXX: V2

    /**
     * Koden forsøger at gøre alle felterne i en spillers program synlige.
     * Den bruger en if-sætning til at bestemme om værdien af register er
     * større end 0 og mindre end eller lig med Player.
     * NO_REGISTERS, så går den gennem hver af spillerne på brættet.
     * @param register
     */
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

    /**
     * Koden går gennem hver af spillerne og gør deres programfelter usynlige.
     */
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

    /**
     * Koden vil få programmet til at fortsætte med at køre, indtil det når slutningen.
     */
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    // XXX: V2
    /**
     * Koden indstiller trintilstanden til sand.
     */
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    // XXX: V2

    /**
     * Koden udføres, indtil brættet er i en step-mode.
     * Hvis den ikke er det, udføres koden igen.
     * Koden vil fortsætte med at køre, indtil brættet er i en step-mode.
     */
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    /**
     * @author Ali Hassan Tariq (s205853)
     * @author Ekkart Kindler
     *
     * Kode forsøger at kontrollere, om den aktuelle spiller har et kort, der kan aktiveres.
     * Hvis der er en, så vil den få sin kommando fra kortets kommandofelt.
     * Den tredje kodelinje udfører en if-sætning, som kontrollerer, om der er en handling på kortet eller ej.
     * Kode forsøger at skabe en ny spiller og tildele dem kommandoen "move".
     * Koden vil udføre kommandoen på den aktuelle spiller, og derefter fortsætte med at gå gennem alle 10 spillere.
     * Hver gang vi går gennem alle spillere, skal vi øge vores tællervariabel med 1.
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
                    for(int i = 0; i < board.getPlayersNumber(); i++)
                        triggerBoardElements(board.getPlayer(i));
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
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
     * @author Christian Kjeldgaard Jensen (s215703)
     * @author Seier Kieldsen (s215708)
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
     * @author Anisa Riaz (s216237)
     * Udbygget med ekstra commands.
     * Implementerer de fire kommandoer som programmet støtter
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
     * @author Anisa Riaz (s216237)
     * @author Ekkart Kindler
     *
     * Her i vores gamecontroller anvender vi rekursion til metoden moveToSpace(), hvilket
     * betyder at metoden kalder på sig selv, dette gør den ved hver spiller der står på et felt, som
     * vi er ved at rykke til med en anden spiller.
     *
     * spilleren skubbes på feltet foran frem, indtil alle spillere der skubber til hinanden er flyttet eller en
     * væg umuliggør flytningen.
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
     * @ Ali Shanoof (S215716)
     * Kode er en metode, der flytter spilleren et felt frem.
     * Kontrollerer først, om der er et nextSpace,
     * i hvilket tilfælde det flytter til det nextSpace med spillerens kurs som destination.
     * Hvis ikke, så sker der intet, og ingen undtagelse bliver kastet på grund af denne check for umulige træk.
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
     * @author Ali Hassan Tariq (s205853)
     * Koden forsøger at flytte spilleren fra deres nuværende position på brættet til en plads,
     * der er tre pladser væk. Det første argument er destinationen for,
     * hvor de vil hen, og det andet argument er,
     * hvilken retning de er på vej i (getNeighbour, moveToSpace).
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
     * @Christian Kjeldgaard Jensen (s215703)
     *  rykker også spilleren via kort
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
     * @Seier Kieldsen (s215708)
     * Uvending med kort
     * */
    public void uTurn(@NotNull Player player){
        if(player != null && player.getHeading() != null){
            player.setHeading(player.getHeading().opposite());
        }
    }
    /**
     * @author Anisa Riaz (s216237)
     * ryk tilbage via kort
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
     * @author Ali Hassan Tariq (s205853)
     * ryk til venstre via kort
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
