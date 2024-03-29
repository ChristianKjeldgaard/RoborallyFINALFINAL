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

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

import dk.dtu.compute.se.pisd.roborally.RoboRally;

import dk.dtu.compute.se.pisd.roborally.dal.GameInDB;
import dk.dtu.compute.se.pisd.roborally.dal.RepositoryAccess;
import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Watchable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * Antal spiller og farverne på objekterne/spillerene.
 *
 * vi opretter en liste og beder
 * den om at holde objekter af typen af henholdsvis Integer og String
 */
public class AppController implements Observer {

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");

    final private RoboRally roboRally;

    private GameController gameController;

    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }
    /**
     * @author Christian Kjeldgaard Jensen (s215703)
     * @author Seier Kieldsen (s215708)
     * @author Ekkart Kindler
     *
     *Koden giver brugeren mulighed for at vælge antallet af spillere i dette spil.
     *Hvis de vælger en mulighed fra en liste, tages de tilbage til deres spilcontrollerklasse,
     *hvor de kan fortsætte med at spille eller gemme deres fremskridt, hvis det ønskes.
     *Koden forsøger at skabe et nyt spil med brugeren.
     *
     * */
    public void newGame() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> result = dialog.showAndWait();

        if (result.isPresent()) {
            if (gameController != null) {
                // The UI should not allow this, but in case this happens anyway.
                // give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    return;
                }
            }

            // XXX the board should eventually be created programmatically or loaded from a file
            //     here we just create an empty board with the required number of players.
             ArrayList<String> boards = new ArrayList<>();
            boards.add("defaultboard");
            boards.add("mainboard");
            ChoiceDialog<String> boardDialog = new ChoiceDialog<>(boards.get(0), boards);
            boardDialog.setTitle("Boards");
            boardDialog.setHeaderText("Select board");
            Optional<String> boardResult = boardDialog.showAndWait();
            Board board = LoadBoard.loadBoard(boardResult.get());
            gameController = new GameController(board);
            int no = result.get();
            for (int i = 0; i < no; i++) {
                Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1));
                board.addPlayer(player);
                player.setSpace(board.getSpace(i % board.width, i));
                player.attach(this);
            }
            // board.setCurrentPlayer(board.getPlayer(0));
            gameController.startProgrammingPhase();
            roboRally.createBoardView(gameController);

            RepositoryAccess.getRepository().createGameInDB(gameController.board);
        }
    }

    /**
     * Giver mulighed for at gemme aktiv spil i database.
     */
    public void saveGame() {
        RepositoryAccess.getRepository().updateGameInDB(gameController.board);
    }
    /**
     * @author Anisa Riaz (s216237)
     * Giver mulighed for at loade det gemte spil fra database.
     * Den opretter en arraylist til at gemme spil-id'er og gentager hvert spil på listen. 
     * For hvert spil viser det en dialog med to muligheder: "Vælg spil" og "Indlæs spil".
     * */
    public void loadGame() {
        List<GameInDB> games = RepositoryAccess.getRepository().getGames();
        List<Integer> gameIds= new ArrayList<>();
        for(GameInDB game: games) {
            gameIds.add(game.id);
        }
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(gameIds.get(0), gameIds);
        dialog.setTitle("Select game");
        dialog.setHeaderText("Select game");
        Optional<Integer> result = dialog.showAndWait();
        if(result.isPresent()){
            for(GameInDB oneGame : games){
                if(oneGame.id == result.get()){
                    Board board = RepositoryAccess.getRepository().loadGameFromDB(oneGame.id);
                    gameController = new GameController(board);
                    roboRally.createBoardView(gameController);
                    for(int i = 0; i < board.getPlayersNumber(); i++){
                        board.getPlayer(i).attach(this);
                    }
                    return;
                }
            }
        }
    }

    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {

            // here we save the game (without asking the user).
            saveGame();

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    /**
     * Herunder ses implementering af exit funtionen i spillet
     * hvor når man trykker på file/exit, kommer en confirmation som
     * kan besvares med Ok eller cancel. Ved tryk på Ok lukker man spillet.
     */
    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    public boolean isGameRunning() {
        return gameController != null;
    }

    /**
     * Koden starter med at kontrollere, om Subjectet er en spiller. 
     * Hvis det er det, så tjekker koden for at se, om der er nogen kontrolpunkter tilbage på brættet, og om den spiller har ramt dem alle.
     * Hvis de har, vises en advarsel med deres navn samt teksten "Vinder fundet" og en bekræftelsesmeddelelse om, at de vandt.
     * @param subject the subject which changed
     */
    @Override
    public void update(Subject subject) {
        if(subject instanceof Player) {
            Player player = (Player) subject;
            if (gameController != null && player.getCheckpointsHit() == gameController.board.getCheckpointsNumber()){
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Winner found");
                alert.setContentText(player.getName() +" has won");
                alert.showAndWait();
                stopGame();
            }
        }
    }

}
