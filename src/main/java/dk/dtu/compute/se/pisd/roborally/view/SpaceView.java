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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.PushPanel;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 60; // 60; // 75;
    final public static int SPACE_WIDTH = 60;  // 60; // 75;

    public final Space space;


    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        if ((space.x + space.y) % 2 == 0) {
            this.setStyle("-fx-background-color: white;");
        } else {
            this.setStyle("-fx-background-color: black;");
        }

        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updatePlayer() {

        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0 );
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90*player.getHeading().ordinal())%360);
            this.getChildren().add(arrow);
        }
    }

    /**
     * @author Christian Kjeldgaard Jensen (s215703)
     * @author Seier Kieldsen (s215708)
     * @author Ekkart Kindler
     * Denne klasse tjekker hvilken fieldAction vi har på feltet hvis nogen, og kalder den rigtige "tegnemetode"
     * */
    @Override
    public void updateView(Subject subject) {
        this.getChildren().clear();
        drawWalls();
        if(space.getActions() != null) {
            Class<?> fieldAction = space.getActions().get(0).getClass();
            if (fieldAction == ConveyorBelt.class) {
                drawConveyorBelt();
            } else if (fieldAction == PushPanel.class) {
                drawPushPanel();
            } else if(fieldAction == Checkpoint.class){
                drawCheckpoints();
            }

        }
        if (subject == this.space) {
            updatePlayer();
        }

    }

    /**
     * @author Christian Kjeldgaard Jensen (s215703)
     * @author Seier Kieldsen (s215708)
     * Tegner checkpoint
     * */
    public void drawCheckpoints(){
        Checkpoint checkpoint = (Checkpoint) space.getActions().get(0);
        Text number = new Text();
        number.setText(Integer.toString(checkpoint.getNumber()));
        number.setFill(Color.GREEN);
        this.getChildren().add(number);
    }

    /**
     * @author Christian Kjeldgaard Jensen (s215703)
     * @author Seier Kieldsen (s215708)
     * */
    private void drawWalls(){
        List<Heading> walls = space.getWalls();
        if(walls != null) {
            for (Heading heading : walls) {
                Pane pane = new Pane();
                Rectangle rectangle = new Rectangle(0,0,SPACE_WIDTH, SPACE_HEIGHT);
                        rectangle.setFill(Color.TRANSPARENT);
                pane.getChildren().add(rectangle);

                Polygon wall = null;
                if(heading.equals(Heading.WEST)) {
                    wall = new Polygon(0, 0, 3, 0, 3, SPACE_HEIGHT, 0, SPACE_HEIGHT);
                }else if(heading.equals(Heading.NORTH)){
                    wall = new Polygon(0, 0, SPACE_WIDTH, 0, SPACE_WIDTH, 3, 0, 3);
                }else if(heading.equals(Heading.EAST)){
                    wall = new Polygon(SPACE_WIDTH, 0, SPACE_WIDTH, SPACE_HEIGHT, SPACE_WIDTH-3, SPACE_HEIGHT, SPACE_WIDTH-3,0);
                }else if(heading.equals(Heading.SOUTH)){
                    wall = new Polygon(0, SPACE_HEIGHT, SPACE_WIDTH, SPACE_HEIGHT, SPACE_WIDTH, SPACE_HEIGHT-3, 0, SPACE_HEIGHT-3);
                }
                wall.setFill(Color.RED);
                pane.getChildren().add(wall);
                getChildren().add(pane);
            }
        }
    }

    /**
     * @author Christian Kjeldgaard Jensen (s215703)
     * @author Seier Kieldsen (s215708)
     * */
    private void drawConveyorBelt(){
        ConveyorBelt conveyorBelt = (ConveyorBelt) space.getActions().get(0);
        Polygon polygon = new Polygon(0.0, 0.0,
                20.0, 40.0,
                40.0, 0.0 );

        polygon.setRotate((90*conveyorBelt.getHeading().ordinal())%360);
        polygon.setFill(Color.GRAY);
        this.getChildren().add(polygon);
    }

    /**
     * @author Ali Shanoof (S215716)
     * @author Ali Hassan Tariq (s205853)
     * Koden er ansvarlig for at tegne push-panelet, der skal bruges i spillet.
     * Koden er en del af PushPanel-klassen.
     * Den opretter et nyt Pane objekt og tilføjer objekter såsom Rectangle,
     * Polygon og fill color ved koordinarerne (0,0) ved brug af setFill() metoden til Pane samling.
     * */)
    private void drawPushPanel(){
        PushPanel pushPanel = (PushPanel) space.getActions().get(0);
        Heading heading = pushPanel.getPushDirection().opposite();
        Pane pane = new Pane();
        Rectangle rectangle = new Rectangle(0,0,SPACE_WIDTH, SPACE_HEIGHT);
        rectangle.setFill(Color.TRANSPARENT);
        pane.getChildren().add(rectangle);

        Polygon wall = null;
        if(heading.equals(Heading.WEST)) {
            wall = new Polygon(0, 0, 3, 0, 3, SPACE_HEIGHT, 0, SPACE_HEIGHT);
        }else if(heading.equals(Heading.NORTH)){
            wall = new Polygon(0, 0, SPACE_WIDTH, 0, SPACE_WIDTH, 3, 0, 3);
        }else if(heading.equals(Heading.EAST)){
            wall = new Polygon(SPACE_WIDTH, 0, SPACE_WIDTH, SPACE_HEIGHT, SPACE_WIDTH-3, SPACE_HEIGHT, SPACE_WIDTH-3,0);
        }else if(heading.equals(Heading.SOUTH)){
            wall = new Polygon(0, SPACE_HEIGHT, SPACE_WIDTH, SPACE_HEIGHT, SPACE_WIDTH, SPACE_HEIGHT-3, 0, SPACE_HEIGHT-3);
        }
        wall.setFill(Color.YELLOW);
        pane.getChildren().add(wall);
        Text text = new Text( 10, 10, "");
        for(Integer integer : pushPanel.getExecuteNumbers()){
            text.setText(text.getText() + integer + " ");
        }
        text.setFill(Color.RED);
        pane.getChildren().add(text);
        getChildren().add(pane);
    }
}
