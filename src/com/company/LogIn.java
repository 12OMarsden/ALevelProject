package com.company;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LogIn extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    // Provides the interface for a user to log onto their account.
    public void start(Stage primaryStage) throws Exception {

        // Code adapted from: https://docs.oracle.com/javafx/2/get_started/form.htm.
        // Sets up the graphic components.
        Stage stage = new Stage();

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));


        Text sceneTitle = new Text("Log In");
        sceneTitle.setFont(Font.font("Ariel", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);
        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label passwordLb = new Label("Password:");
        grid.add(passwordLb, 0, 2);
        PasswordField passwordTextField = new PasswordField();
        grid.add(passwordTextField, 1, 2);

        Button signInBtn = new Button("Sign in");
        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        hBox.getChildren().add(signInBtn);
        grid.add(hBox, 1, 4);

        signInBtn.setOnAction(event -> {
            // Retrieves the text that was in the text fields.
            String username = userTextField.getText();
            String password = passwordTextField.getText();
            //if (accountChecker(password, username)) {
                stage.close();
                MainScreenGui gui = new MainScreenGui();
                try {
//                    gui.run(stage, adminCheck(username));
                    gui.run(stage, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            //} else {
                new PopUpWindow("Username or password was incorrect");
            //}

        });

        Scene scene = new Scene(grid, 300, 275);
        stage.setScene(scene);

        stage.show();
    }

    // Will return whether or not a username-password pair exists in the database.
    public boolean accountChecker(String password, String username) {
        String query = "SELECT CASE WHEN EXISTS (SELECT * FROM [players] WHERE password = '" + password + "' AND username = '" + username + "' ) THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT) END";
        return sqlBooleanRetrieval(query);
    }

    // Will return whether of not a given username has administrator access.
    public boolean adminCheck(String username) {
        String query = "SELECT Admin FROM players WHERE Username='" + username + "'";
        return sqlBooleanRetrieval(query);
    }

    // A function for returning a boolean representing if an entity in a table exists or not.
    public boolean sqlBooleanRetrieval(String query) {
        int i = 0;
        Statement stmt;
        ResultSet rs;
        try {
            // Create and execute an SQL statement that returns some data.
            // This will return a '1' if the username and matching password exist in the database.
            stmt = DatabaseConnector.connection.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            i = rs.getInt(1);
        } catch (SQLException el) {
            el.printStackTrace();
        }

        // If 1 is returned then it means there are instances of the given fields so it can be used as a new account username.
        if (i == 1) {
            return true;
        } else {
            return false;
        }
    }


}
