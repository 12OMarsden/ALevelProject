package com.company;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Created by 12OMarsden on 01/04/2019.
 */
public class PopUpWindow {

    // Simple method to create a pop up window when a message needs to be displayed.
    public PopUpWindow(String message) {

        // Sets up the graphic components.
        Stage stage = new Stage();

        Label label = new Label(message);
        Button exit = new Button("Back");
        VBox vBox = new VBox(label, exit);

        Scene scene = new Scene(vBox);
        scene.getStylesheets().add(getClass().getResource("Stylesheet.css").toExternalForm());

        //If the enter key is pressed the stage will close.
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                stage.close();
            }
        });
        exit.setOnAction(event -> stage.close());

        stage.setScene(scene);
        // No other parts of the program will run whilst this stage is open.
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

}
