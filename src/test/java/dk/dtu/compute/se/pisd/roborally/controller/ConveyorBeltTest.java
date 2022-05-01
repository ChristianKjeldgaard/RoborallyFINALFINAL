package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ConveyorBeltTest {
    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private GameController gameController;


    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        gameController = new GameController(board);
        for (int i = 0; i < 3; i++) {
            Player player = new Player(board, null,"Player " + i);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }

    @Test
    void hitConveyorBelt () {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        List<FieldAction> actionList = new ArrayList<>();
        actionList.add(new ConveyorBelt(Heading.SOUTH));
        board.getSpace(2,4).setFieldActions(actionList);
        current.setSpace(board.getSpace(1,4));
        current.setHeading(Heading.EAST);
        gameController.moveForward(current);
        gameController.triggerBoardElements(current);
        Assertions.assertEquals(board.getSpace(2,5), current.getSpace(), "Should be on 2,5");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Should be facing south");
        Assertions.assertNull(board.getSpace(2, 4).getPlayer(),"Player should be on the checkpoint square");
    }

}
