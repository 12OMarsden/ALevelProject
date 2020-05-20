package com.company;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by 12OMarsden on 19/09/2018.
 */
public class AllAccounts {

    // Returns a gridPane populated with all account username and passwords.
    public GridPane start() {

        // Set up the grid that will show the inventory in a table
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.TOP_CENTER);
        //Set width of 4 columns
        for (int i = 0; i < 2; i++) {
            gridPane.getColumnConstraints().add(new ColumnConstraints(100));
        }

        Label label1 = new Label("Username");
        label1.setStyle("-fx-font-weight: bold");
        Label label2 = new Label("Password");
        label2.setStyle("-fx-font-weight: bold");


        gridPane.setGridLinesVisible(true);

        updateAllElements(gridPane);

        return gridPane;
    }

    // Populates the given gridPane with the
    public void updateAllElements(GridPane gridPane) {

        // 'updateTable.UpdateTable()' will return each account's details into an element of 'accounts'.
        UpdateTable updateTable = new UpdateTable();
        ArrayList<String> accounts = updateTable.UpdateTable();

        for (int i = 0; i < accounts.size(); i++) {
            for (int j = 0; j < 2; j++) {
                String [] individualDetails = accounts.get(i).split(",");
                gridPane.add(new Label(individualDetails[0]), 0, i + 1);
                gridPane.add(new Label(individualDetails[1]), 1, i + 1);
            }
        }
    }

}
