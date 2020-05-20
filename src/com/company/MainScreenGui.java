package com.company;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.JSONException;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class MainScreenGui {

    TabPane tabPane;
    Stage primaryStage;

    // Provides the main interface that will not be changed when matches are added.
    public void run(Stage stage, Boolean isAdmin) throws Exception {

        //Disables ability for the tabPane to request focus.
        tabPane = new TabPane() {
            public void requestFocus() {
            }
        };
        tabPane.setPrefHeight(600);
        // Sets up interface graphics.
        Button add_match = new Button("Add Match");
        Button stopWatch = new Button("Display Stopwatch");
        Button accountManager = new Button("Account Manager");
        if (!isAdmin) {
            accountManager.setDisable(true);
        }
        Button logOut = new Button("Log Out");

        // Code adapted from: https://stackoverflow.com/questions/43793497/how-to-set-a-tooltip-on-a-javafx-pane.
        // Necessary as disabled buttons will not show a tooltips.
        HBox createAccountHolder = new HBox(accountManager);
        Tooltip tooltip = new Tooltip("Only available for system administrators");
        Tooltip.install(createAccountHolder, tooltip);

        HBox hBox = new HBox(add_match, stopWatch, accountManager, logOut);
        hBox.setSpacing(20);
        hBox.setAlignment(Pos.CENTER);
        VBox vBox = new VBox(hBox, tabPane);
        vBox.setSpacing(20);
        Scene scene = new Scene(vBox, 600, 600);
        //Sets the scene to a pre-made style.
        scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());

        // This sets the default style of the tabPane to be a different preset.
        tabPane.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());
        //This means pressing the arrow keys won't change the tab. Necessary as arrow keys are used for scoring.
        tabPane.setFocusTraversable(false);


        //Sets what to do when each button is clicked.
        add_match.setOnAction(event -> {
            Tab tab = new Tab();
            tabPane.getTabs().add(tab);
            matchSetUp(new SquashLevelPlayer[2], tab);
        });

        stopWatch.setOnAction(event -> displayStopWatch(false));

        accountManager.setOnAction(event -> accountManager(new Tab()));

        logOut.setOnAction(event -> {
            LogIn logIn = new LogIn();
            primaryStage.close();
            try {
                logIn.start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //Sets up the primary stage.
        stage.setTitle("Squash Marking System");
        stage.setScene(scene);
        stage.show();
        this.primaryStage = stage;

    }

    // Provides the interface for user selection of creating or deleting an account.
    public void accountManager(Tab tab) {

        tabPane.getTabs().add(tab);
        Button createAccount = new Button("Create an account");
        Button deleteAccount = new Button("Delete an account");

        HBox hBox1 = new HBox(createAccount, deleteAccount);
        hBox1.setSpacing(10);
        hBox1.setPadding(new Insets(20));
        hBox1.setAlignment(Pos.CENTER);

        tab.setContent(hBox1);
        tab.setText("Account Management");

        createAccount.setOnAction(event1 -> createAccountScreen(tab));

        deleteAccount.setOnAction(event1 -> deleteAccountScreen(tab));
    }

    // Provides the interface for creating a new account as an admin.
    public void createAccountScreen(Tab tab) {

        // Sets up the graphic components.
        GridPane rootNode = new GridPane();

        rootNode.setAlignment(Pos.CENTER);
        rootNode.setHgap(10);
        rootNode.setVgap(10);
        rootNode.setPadding(new Insets(15));

        Text sceneTitle = new Text("Create Account");
        sceneTitle.setFont(javafx.scene.text.Font.font("Ariel", FontWeight.NORMAL, 20));
        rootNode.add(sceneTitle, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        rootNode.add(userName, 0, 1);
        TextField userTextField = new TextField();
        rootNode.add(userTextField, 1, 1);

        Label passwordLabel = new Label("Password:");
        rootNode.add(passwordLabel, 0, 2);
        PasswordField passwordTextField = new PasswordField();
        rootNode.add(passwordTextField, 1, 2);

        Button createAccountBtn = new Button("Create Account");
        createAccountBtn.setAlignment(Pos.BOTTOM_RIGHT);
        rootNode.add(createAccountBtn, 1, 4);

        CheckBox isAdmin = new CheckBox("Administrator Account");
        rootNode.add(isAdmin, 0, 3, 2, 1);

        // Retrieves an image of an arrow and sets it as the graphic to a button.
        javafx.scene.image.Image imageReturn = new javafx.scene.image.Image(getClass().getResourceAsStream("go-back-left-arrow.png"));
        Button returnBtn = new Button();
        returnBtn.setGraphic(new ImageView(imageReturn));
        HBox hBox1 = new HBox(10);
        hBox1.setAlignment(Pos.BOTTOM_LEFT);
        hBox1.getChildren().add(returnBtn);
        rootNode.add(hBox1, 0, 4);

        createAccountBtn.setOnAction(event -> {
            // Retrieves the text that was in the text fields.
            String username = userTextField.getText();
            String password = passwordTextField.getText();
            // Checks if the 'isAdmin' check box is selected.
            int adminAccount = 0;
            if (isAdmin.isSelected()) {
                adminAccount = 1;
            }
            if (!username.equals("") && password.length() > 3 && UsernamePreExistCheck(username)) {
                // Carries out the necessary query to add a record to the 'players' table in my database.
                new SQLQueryExecution("Insert into players (Username, Password, Admin) values ('" + username + "', '" + password + "', " + adminAccount + ")");
                new PopUpWindow("Account successfully created");
                tabPane.getTabs().remove(tab);
            } else {
                new PopUpWindow("Username or password is invalid");
            }

        });

        // Closes the stage when the 'returnBtn' button is pressed.
        returnBtn.setOnAction(event -> accountManager(tab));

        tab.setContent(rootNode);
    }

    // Provides the interface for deleting an account as an admin.
    public void deleteAccountScreen(Tab tab) {

        // Sets up the graphic components.
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text sceneTitle = new Text("Delete Account");
        sceneTitle.setFont(javafx.scene.text.Font.font("Ariel", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);

        Label userNameLabel = new Label("User Name:");
        grid.add(userNameLabel, 0, 1);
        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Button deleteAccountBtn = new Button("Delete Account");

        deleteAccountBtn.setAlignment(Pos.BOTTOM_RIGHT);
        grid.add(deleteAccountBtn, 1, 2);

        // Retrieves an image of an arrow and sets it as the graphic to a button.
        javafx.scene.image.Image arrowImage = new javafx.scene.image.Image(getClass().getResourceAsStream("go-back-left-arrow.png"));
        Button returnBtn = new Button();
        returnBtn.setGraphic(new ImageView(arrowImage));
        HBox hBox1 = new HBox(10);
        hBox1.setAlignment(Pos.BOTTOM_LEFT);
        hBox1.getChildren().add(returnBtn);
        grid.add(hBox1, 0, 2);

        // Retrieves a gripPane populated with every account's details.
        AllAccounts allAccounts = new AllAccounts();
        GridPane accounts = allAccounts.start();
        grid.add(accounts, 0, 3, 3, 1);

        tab.setContent(grid);

        deleteAccountBtn.setOnAction(event -> {
            String username = userTextField.getText();
            if (!UsernamePreExistCheck(username)) {
                new SQLQueryExecution("DELETE FROM players WHERE Username = '" + username + "'");
                new PopUpWindow("Account successfully deleted");
                tabPane.getTabs().remove(tab);
            } else {
                new PopUpWindow("Username not found");
            }
        });

        returnBtn.setOnAction(event -> accountManager(tab));

    }

    // Checks if a given username is already in the 'players' table.
    public boolean UsernamePreExistCheck(String username) {
        int i = 0;
        Boolean valid;
        Statement stmt;
        ResultSet rs;
        try {
            // Create and execute an SQL statement that returns some data.
            // This will return a '1' if the username already exists meaning it cannot be used.
            String SQL = "SELECT CASE WHEN EXISTS (SELECT * FROM [players] WHERE username = '" + username + "' ) THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT) END";
            stmt = DatabaseConnector.connection.createStatement();
            rs = stmt.executeQuery(SQL);
            rs.next();
            i = rs.getInt(1);
        } catch (SQLException el) {
            el.printStackTrace();
        }

        // If 0 is returned then it means there are no instances of the given username so it can be used as a new account username.
        if (i == 0) valid = true;
        else valid = false;

        return valid;
    }

    // Provides the interface for the user to set up a match to score.
    public void matchSetUp(SquashLevelPlayer[] selectedPlayers, Tab tab) {

        // Sets up the Grid Pane and populates it.
        GridPane rootNode = new GridPane();
        rootNode.setPadding(new Insets(15));
        rootNode.setHgap(5);
        rootNode.setVgap(50);
        rootNode.setAlignment(Pos.CENTER);

        rootNode.add(new Label("Player A: "), 0, 0);
        TextField playerAName = new TextField();
        playerAName.setPromptText("E.G. Oliver");
        rootNode.add(playerAName, 1, 0);

        rootNode.add(new Label("Player B: "), 0, 1);
        TextField playerBName = new TextField();
        playerBName.setPromptText("E.G. Xavier");
        rootNode.add(playerBName, 1, 1);

        // 'matchSetUp' is passed a 'SquashLevelPlayer' array so that the names of the players that are found on
        // Squash Levels can be displayed on this page. However, the user may not choose any players on squash levels
        // so this is not always necessary.
        if (selectedPlayers[0] != null && selectedPlayers[1] != null) {
            playerAName.setText(selectedPlayers[0].getName());
            playerBName.setText(selectedPlayers[1].getName());
        }

        rootNode.add(new Label("Points per game: "), 0, 2);
        TextField pointsPerGame = new TextField();
        // Forces pointsPerGame to only hold numeric values.
        // Adapted from https://stackoverflow.com/questions/7555564/what-is-the-recommended-way-to-make-a-numeric-textfield-in-javafx
        pointsPerGame.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                pointsPerGame.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        pointsPerGame.setPromptText("E.G. 11");
        rootNode.add(pointsPerGame, 1, 2);

        rootNode.add(new Label("Games per match: "), 0, 3);
        TextField gamesNeededToWin = new TextField();
        // Forces gamesNeededToWin to only hold numeric values.
        // Adapted from https://stackoverflow.com/questions/7555564/what-is-the-recommended-way-to-make-a-numeric-textfield-in-javafx
        gamesNeededToWin.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                gamesNeededToWin.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        gamesNeededToWin.setPromptText("E.G. 5");
        rootNode.add(gamesNeededToWin, 1, 3);

        Button setUpMatchBtn = new Button("Set up match");
        setUpMatchBtn.setStyle("-fx-font-size:20");
        rootNode.add(setUpMatchBtn, 0, 6);
        Button playerSearchBtn = new Button("Search for players on Squash Levels");
        rootNode.add(playerSearchBtn, 1, 6);

        //Sets up the check boxes so only 1 box out of a pair can be selected.
        CheckBox par = new CheckBox("PAR");
        CheckBox hiHo = new CheckBox("HandInHandOut");
        par.setSelected(true);
        par.setOnAction(event -> {
            if (par.isSelected()) {
                hiHo.setSelected(false);
            } else {
                hiHo.setSelected(true);
            }
        });
        hiHo.setOnAction(event -> {
            if (hiHo.isSelected()) {
                par.setSelected(false);
            } else {
                par.setSelected(true);
            }
        });
        HBox hBox = new HBox(par, hiHo);
        hBox.setSpacing(10);
        rootNode.add(new Label("Scoring system: "), 0, 4);
        rootNode.add(hBox, 1, 4);

        CheckBox sd = new CheckBox("Sudden Death");
        CheckBox twoClear = new CheckBox("2 Clear");
        twoClear.setSelected(true);
        twoClear.setOnAction(event -> {
            if (twoClear.isSelected()) {
                sd.setSelected(false);
            } else {
                sd.setSelected(true);
            }
        });
        sd.setOnAction(event -> {
            if (sd.isSelected()) {
                twoClear.setSelected(false);
            } else {
                twoClear.setSelected(true);
            }
        });
        HBox hBox1 = new HBox(twoClear, sd);
        hBox1.setSpacing(10);
        rootNode.add(new Label("Win condition at draw point: "), 0, 5);
        rootNode.add(hBox1, 1, 5);

        //Sets what to do when each button is clicked.
        setUpMatchBtn.setOnAction(e -> matchCall(playerAName, playerBName, pointsPerGame, gamesNeededToWin, par, twoClear, tab, selectedPlayers));
        playerSearchBtn.setOnAction(event -> playerSearch(playerAName, playerBName, tab));
        //Sets the focus for event listeners to be on the gridPane so that any key clicks for example are acted on here
        //and not for example by the tabPane.
        rootNode.requestFocus();
        rootNode.setOnKeyPressed(event -> {
            // When enter is pressed 'matchCall' is called.
            if (event.getCode() == KeyCode.ENTER) {
                matchCall(playerAName, playerBName, pointsPerGame, gamesNeededToWin, par, twoClear, tab, selectedPlayers);
            }
        });

        //Sets up the tab and adds it to the tabPane.
        tab.setText("Match Set Up");
        tab.setContent(rootNode);
    }

    //Provides the interface for the user to search and select players from the Squash Levels DB.
    public void playerSearch(TextField playerAName, TextField playerBName, Tab tab) {

        // The players that the user selects will be stored in this array.
        SquashLevelPlayer[] selectedPlayers = new SquashLevelPlayer[2];

        // Sets up the Grid Pane and populates it.
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setVgap(10);
        gridPane.setHgap(15);

        String playerA = playerAName.getText();
        String playerB = playerBName.getText();

        Label label = new Label("Player A");
        gridPane.add(label, 0, 0);
        gridPane.add(playerAName, 1, 0);

        Label label1 = new Label("Player B");
        gridPane.add(label1, 3, 0);
        gridPane.add(playerBName, 4, 0);

        Button searchPlayerA = new Button("Search for " + playerA);
        Button searchPlayerB = new Button("Search for " + playerB);
        gridPane.add(searchPlayerA, 0, 1, 2, 1);
        gridPane.add(searchPlayerB, 3, 1, 2, 1);

        // Each time a key is pressed in the text fields, the button displaying what is in the text field updates to
        // show the same text
        playerAName.setOnKeyReleased(event -> searchPlayerA.setText("Search for " + playerAName.getText()));
        playerBName.setOnKeyReleased(event -> searchPlayerB.setText("Search for " + playerBName.getText()));
        Button go = new Button("Add players to match");
        Button back = new Button("Cancel");
        HBox hBox = new HBox(go, back);
        hBox.setSpacing(30);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(gridPane);
        borderPane.setBottom(hBox);

        //Sets what to do when each button is clicked.
        searchPlayerA.setOnAction(event -> {
            ScrollPane playerList = searchPlayer(0, playerAName.getText(), selectedPlayers);
            gridPane.add(playerList, 0, 2, 3, 1);

        });
        searchPlayerB.setOnAction(event -> {
            ScrollPane playerList = searchPlayer(1, playerBName.getText(), selectedPlayers);
            gridPane.add(playerList, 2, 2, 3, 1);
        });

        go.setOnAction(event -> {
            // This button will only parse the players to 'matchSetUp' if both player A and player B have players
            // assigned to them.
            if (selectedPlayers[0] != null && selectedPlayers[1] != null) {
                matchSetUp(selectedPlayers, tab);
            }
        });
        back.setOnAction(event -> {
            // This button will parse an empty 'SquashLevelPlayer' array to 'matchSetUp' as the user has chosen to
            // cancel their entry.
            SquashLevelPlayer[] noPlayers = new SquashLevelPlayer[2];
            matchSetUp(noPlayers, tab);
        });

        tab.setContent(borderPane);
    }

    // Returns a scrollPane, populated with buttons which represent players found from a seaaddPorch in the Squash Levels
    // database.
    public ScrollPane searchPlayer(int playerNo, String playerName, SquashLevelPlayer[] selectedPlayers) {

        // Populates a 'SquashLevelPlayer' array list with players from calling SquashLevelPlayerSearch.
        SquashLevelPlayerSearch squashLevelPlayerSearch = new SquashLevelPlayerSearch();
        ArrayList<SquashLevelPlayer> squashLevelPlayerArrayList = new ArrayList<>();
        try {
            squashLevelPlayerArrayList = squashLevelPlayerSearch.main(playerName);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        // Adds an array list of toggle buttons to a scrollPane. Each button represents a player.
        // e.g. the button[5] will read 'Richard Bickers: 1098 (AVON)' and the 'squashLevelFoundPlayer' with an index of 5 in
        // 'squashLevelPlayerArrayList' will hold the details for that player.
        ToggleButton[] buttons = new ToggleButton[squashLevelPlayerArrayList.size()];
        VBox vBox = new VBox();
        vBox.setSpacing(2);
        for (int i = 0; i < squashLevelPlayerArrayList.size(); i++) {
            ToggleButton button = new ToggleButton(squashLevelPlayerArrayList.get(i).getName() + ": " + squashLevelPlayerArrayList.get(i).level + " (" + squashLevelPlayerArrayList.get(i).county + ")");
            buttons[i] = button;
            // Necessary so that 'i' and 'squashLevelPlayerArrayList' can be referenced in the lambda expression.
            // This is essentially making a temporary final version of each variable.
            int counter = i;
            ArrayList<SquashLevelPlayer> finalSquashLevelPlayerArrayList = squashLevelPlayerArrayList;

            //Sets what to do when a button in 'buttons' is clicked.
            buttons[i].setOnAction(event1 -> {
                for (int j = 0; j < finalSquashLevelPlayerArrayList.size(); j++) {
                    // Deselects all other buttons when one is pressed.
                    if (counter != j) {
                        buttons[j].setSelected(false);
                    }
                }
                // Sets 1 of the selected players to the selected foundPlayer.
                selectedPlayers[playerNo] = finalSquashLevelPlayerArrayList.get(counter);
            });

            vBox.getChildren().add(buttons[i]);
        }
        // Sets up the scrollPane, populating it with all the buttons in.
        ScrollPane scrollPane = new ScrollPane(vBox);
        scrollPane.setPadding(new Insets(3));
        return scrollPane;
    }

    // Creates an instance of 'Match' using the variables that the user entered on hte 'matchSetUp' screen.
    public void matchCall(TextField playerAName, TextField playerBName, TextField pointsPerGame, TextField gamesNeededToWin, CheckBox par, CheckBox twoClear, Tab tab, SquashLevelPlayer[] selectedPlayers) {

        // Retrieves which checkbox has been selected and sets the String variables to appropriate values accordingly.
        String PARorHiHo, SDor2C;
        if (par.isSelected()) {
            PARorHiHo = "PAR";
        } else {
            PARorHiHo = "HiHo";
        }
        if (twoClear.isSelected()) {
            SDor2C = "2C";
        } else {
            SDor2C = "SD";
        }

        Match match = new Match(
                playerAName.getText(),
                playerBName.getText(),
                Integer.valueOf(pointsPerGame.getText()),
                Integer.valueOf(gamesNeededToWin.getText()),
                PARorHiHo,
                SDor2C,
                selectedPlayers
        );
        firstService(match, tab);
    }

    // Allows the user to choose which player serves first and edits 'match' accordingly.
    public void firstService(Match match, Tab tab) {

        // Sets up the graphic components.
        String name = match.players[0].getName();
        Button playerA = new Button(match.players[0].getName());
        Button playerB = new Button(match.players[1].getName());
        playerA.setStyle("-fx-font-size:40");
        playerB.setStyle("-fx-font-size:40");

        Button stopWatch = new Button("Display Stopwatch");
        stopWatch.setAlignment(Pos.BOTTOM_RIGHT);
        stopWatch.setOnAction(event1 -> displayStopWatch(true));

        Label label = new Label("Which player is serving first:");
        VBox vBox = new VBox(label, playerA, playerB, stopWatch);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(20);

        tab.setContent(vBox);

        // Pressing the arrow keys or the respective button will do the same thing.
        playerA.setOnAction(event -> {
            match.setCurrentServer(0);
            scoreBoards(match, tab);
        });
        playerB.setOnAction(event -> {
            match.setCurrentServer(1);
            scoreBoards(match, tab);
        });

        vBox.requestFocus();

        vBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) {
                match.setCurrentServer(0);
                scoreBoards(match, tab);
            }
            if (event.getCode() == KeyCode.DOWN) {
                match.setCurrentServer(1);
                scoreBoards(match, tab);
            }
        });
    }

    // Creates, populated and adds the 2 scoreboards to a 'MatchScoreBoards' object to allow efficient parsing later on.
    public void scoreBoards(Match match, Tab tab) {

        // Sets up both scoreboards, populating them with the player names and game's won count.
        GridPane pointScoreBoard = new GridPane();
        pointScoreBoard.add(new Label(match.players[0].getName() + " 0"), 0, 0);
        pointScoreBoard.add(new Label(match.players[1].getName() + " 0"), 1, 0);
        pointScoreBoard.setPadding(new Insets(20));
        pointScoreBoard.setHgap(10);
        pointScoreBoard.setAlignment(Pos.TOP_CENTER);

        GridPane gameScoreBoard = new GridPane();

        gameScoreBoard.add(new Label(match.players[0].getName()), 0, 0);
        gameScoreBoard.add(new Label(match.players[1].getName()), 0, 1);
        gameScoreBoard.setPadding(new Insets(20));

        // Sets up the graphic components.
        Button left = new Button(match.players[0].getName() + " +");
        Button right = new Button(match.players[1].getName() + " +");
        left.setStyle("-fx-font-size:25");
        right.setStyle("-fx-font-size:25");
        left.setPrefWidth(primaryStage.getWidth() / 5);
        right.setPrefWidth(primaryStage.getWidth() / 5);
        left.setWrapText(true);
        right.setWrapText(true);

        BorderPane borderPane = new BorderPane();
        ScrollPane scrollPane = new ScrollPane(borderPane);
        borderPane.setCenter(pointScoreBoard);
        borderPane.setTop(gameScoreBoard);
        borderPane.setLeft(left);
        borderPane.setRight(right);

        // Creates an instance of 'matchScoreBoards' which hold both scoreboards. This allows more efficient parsing as
        // both scoreboards are always needed together.
        MatchScoreBoards matchScoreBoards = new MatchScoreBoards(pointScoreBoard, gameScoreBoard, scrollPane, left, right);

        // Retrieves the names of the players and sets them as the tab title.
        String names = match.players[0].getName() + " vs " + match.players[1].getName();
        tab.setText(names);

        tab.setContent(scrollPane);
        // Sets the currently selected tab to the one that this function references.

        tabPane.getSelectionModel().select(tab);

        handoutPopUp(match.getCurrentServer(), match, tab, matchScoreBoards);
    }

    // Makes application actions affect the scoreboards.
    public void scoreBoardActions(Match match, Tab tab, MatchScoreBoards matchScoreBoards) {

        matchScoreBoards.scrollPane.requestFocus();

        matchScoreBoards.scrollPane.setOnKeyPressed(event -> {
            matchScoreBoards.scrollPane.setVvalue(1.0);
            if (event.getCode() == KeyCode.LEFT) {
                addPoints(0, match, tab, matchScoreBoards);
            }
            if (event.getCode() == KeyCode.RIGHT) {
                addPoints(1, match, tab, matchScoreBoards);
            }
            if (event.getCode() == KeyCode.BACK_SPACE) {
                undoScoreBoard(match, matchScoreBoards.pointScoreBoard);
            }
        });

        matchScoreBoards.left.setOnAction(event -> addPoints(0, match, tab, matchScoreBoards));
        matchScoreBoards.right.setOnAction(event -> addPoints(1, match, tab, matchScoreBoards));
    }

    // Adds 1 to a players point score total and calls a scoreboard update to reflect the changes made.
    public void addPoints(int playerId, Match match, Tab tab, MatchScoreBoards matchScoreBoards) {

        //Scrolls the scrollPane down if needed.
        matchScoreBoards.scrollPane.setVvalue(1.0);
        //Adds 1 to the referenced players point score.
        match.addPoints(playerId);
        if (match.getHandout()) {
            handoutPopUp(playerId, match, tab, matchScoreBoards);
        } else {
            // Sets the service side of the player to be the opposite of what it just was.
            if (match.players[playerId].getServiceSide() == 'L') {
                match.players[playerId].setServiceSide('R');
            } else {
                match.players[playerId].setServiceSide('L');
            }

            // Updates the scoreboard's with the new scores.
            updateScoreBoard(matchScoreBoards, match, playerId, tab);
        }
    }

    // Deducts a point from the player who scored last and alters the necessary variables to allow the game to continue
    // as normal after.
    public void undoScoreBoard(Match match, GridPane gridPane) {

        // Retrieves the total number of nodes in the gridPane
        int indexNo = numNodes(gridPane);

        // IF statement stops grid headers being deleted
        if (indexNo > 1) {
            // If any node is in the 1st column then it must be a score of player A. If it is the 2nd column it must be
            // a score of player B.
            int player = gridPane.getColumnIndex(gridPane.getChildren().get(indexNo));
            int previousPlayer = gridPane.getColumnIndex(gridPane.getChildren().get(indexNo - 1));

            // If the last player to score a point is different to the second to last player to score a point then the
            // the match will be in handout status again.
            if (player != previousPlayer) {
                match.setHandout(true);
            } else {
                match.setHandout(false);
            }

            // This removes the most recently added node in the gridPane.
            gridPane.getChildren().remove(indexNo);

            if ((!match.getHandout() && match.getPARorHiHo().equals("HiHo")) || match.getPARorHiHo().equals("PAR")) {
                // This takes off a point from the player that scored last.
                match.players[player].setPointScore(match.players[player].getPointScore() - 1);
            }
        }
    }

    // Code adapted from: 'https://stackoverflow.com/questions/20766363/get-the-number-of-rows-in-a-javafx-gridpane'.
    // Retrieves the number of nodes in a gridPane at any one time.
    public int numNodes(GridPane pointScoreBoard) {

        int numRows = pointScoreBoard.getRowConstraints().size();
        for (int i = 0; i < pointScoreBoard.getChildren().size(); i++) {
            Node child = pointScoreBoard.getChildren().get(i);
            if (child.isManaged()) {
                Integer rowIndex = GridPane.getRowIndex(child);
                if (rowIndex != null) {
                    numRows = Math.max(numRows, rowIndex + 1);
                }
            }
        }

        return numRows;
    }

    // Updates the scoreboards with the new fields.
    public void updateScoreBoard(MatchScoreBoards matchScoreBoards, Match match, int player, Tab tab) {

        // Retrieves the depth of the pointScoreBoard.
        int gridDepth = numNodes(matchScoreBoards.pointScoreBoard);

        // The game scoreboard width is always proportional to the sum of both players total games won in a match.
        int gridWidth = match.players[0].getGameScore() + match.players[1].getGameScore() + 1;

        // If the match was set up to "2C" this means a player must win by 2 clear points over their opponent. This
        // checks this and applies the suitable procedures to take account for it.
        if (match.getSDor2C().equals("2C")) {
            match.checkFor2C();
        }

        // Creates a label with the players new score along with which side to serve from.
        Label newScore = new Label(Integer.toString(match.players[player].getPointScore()) + Character.toString(match.players[player].getServiceSide()));
        newScore.setStyle("-fx-font-size:15");

        // It adds the label to pointScoreBoard at the corresponding depth calculated earlier.
        matchScoreBoards.pointScoreBoard.add(newScore, player, gridDepth);
        match.setCurrentServer(player);

        if (match.checkGameWon(player)) {

            // Adds 1 to the players game score if the new added point means they won the game.
            match.players[player].setGameScore(match.players[player].getGameScore() + 1);

            // Adds both players score for the game to a new column in 'gameScoreBoard'.
            for (int i = 0; i < 2; i++) {
                matchScoreBoards.gameScoreBoard.add(new Label(Integer.toString(match.players[i].getPointScore())), gridWidth, i);
            }

            // Saves the point scoreboard within the 'Match' instance before it gets cleared. This is for saving the entire match record to a file later.

            if (match.checkMatchWon(player)) {
                matchWin(match, tab, matchScoreBoards);
            } else {

                // If neither a game has been won but not the match, the point scoreboard is cleared; ready for the next game to be set up.
                matchScoreBoards.pointScoreBoard.getChildren().clear();
                match.setPreviousGameWinner(player);
                gameWin(player, match);

                // As the entire point scoreboard was cleared, the players names and game score count need to be added back to it.
                for (int i = 0; i < 2; i++) {
                    matchScoreBoards.pointScoreBoard.add(new Label(match.players[i].getName() + " " + match.players[i].getGameScore()), i, 0);
                }
                match.resetPoints();
                handoutPopUp(match.getPreviousGameWinner(), match, tab, matchScoreBoards);
            }
        }
    }

    // Provides the end match interface after the match is over, giving the players options with what to do with the result.
    public void matchWin(Match match, Tab tab, MatchScoreBoards matchScoreBoards) {

        // Retrieves the winner of the match.
        String winner;
        if (match.getMatchWinner() == 0) {
            winner = match.players[0].getName();
        } else {
            winner = match.players[1].getName();
        }
        HBox hBox = new HBox(new Label(winner + " won! :" + match.getMatchRecord()));
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(50);

        Button squashLevelsButton = new Button("Add match records to Squash Levels");
        Button saveMatch = new Button("Save match to .txt file");
        Button deleteMatch = new Button("Delete Match");

        // If the user did not set up the matches with players from Squash Levels, the 'squashLevelsButton' button must not be accessible.
        if (!match.getLinkedToSquashLevel()) squashLevelsButton.setDisable(true);

        VBox vBox = new VBox(hBox, squashLevelsButton, saveMatch, deleteMatch);
        vBox.setSpacing(25);
        vBox.setAlignment(Pos.CENTER);

        deleteMatch.setOnAction(event -> tabPane.getTabs().remove(tab));

        squashLevelsButton.setOnAction(event -> addMatchToSquashLevels(match, squashLevelsButton));

        saveMatch.setOnAction(event -> saveMatchToTxtFile(matchScoreBoards, match));

        tabPane.getSelectionModel().select(tab);

        tab.setContent(vBox);

    }

    // Gives a player who won a point off of the other players serve, the choice of which side to serve from.
    public void handoutPopUp(int player, Match match, Tab tab, MatchScoreBoards matchScoreBoards) {

        // If a player wins a point they get to serve for the next rally.
        // A 'handout' is when a player has won a point off of the others player serve. This means they get to decide
        // which side they want to serve from. Winning a point off of your own serve means you have to serve the next
        // point from the opposite side. This is not a handout and the player has nop choice in serving side.

        // Every time 'handoutPopUp' is called the game will no longer be in a handout status.
        match.setHandout(false);

        // Creates a pop up window.
        Stage handoutStage = new Stage();
        // This means that this stage is the only one that can be used while it is open.
        handoutStage.initModality(Modality.APPLICATION_MODAL);

        // Sets up the graphic components.
        Button left = new Button("LEFT");
        Button right = new Button("RIGHT");
        left.setStyle("-fx-font-size:40");
        right.setStyle("-fx-font-size:40");

        HBox hBox = new HBox(left, right);
        hBox.setSpacing(20);
        Label label = new Label("Which side is the player serving from?");
        VBox vBox = new VBox(label, hBox);
        vBox.setSpacing(10);

        // Pressing the arrow keys or the respective button will do the same thing.
        left.setOnAction(event -> handoutButtonAction(player, match, tab, matchScoreBoards, 'L', handoutStage));
        right.setOnAction(event -> handoutButtonAction(player, match, tab, matchScoreBoards, 'R', handoutStage));

        hBox.requestFocus();

        hBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT) {
                handoutButtonAction(player, match, tab, matchScoreBoards, 'L', handoutStage);
            }
            if (event.getCode() == KeyCode.RIGHT) {
                handoutButtonAction(player, match, tab, matchScoreBoards, 'R', handoutStage);
            }
        });

        Scene scene = new Scene(vBox);
        scene.getStylesheets().add(getClass().getResource("Stylesheet.css").toExternalForm());
        handoutStage.setScene(scene);
        // Shows the stage and won't do anything else until the window is closed
        handoutStage.showAndWait();
    }

    // This updates the 'match' details according to the selection the user made on the 'handoutPopUp'.
    public void handoutButtonAction(int player, Match match, Tab tab, MatchScoreBoards matchScoreBoards, char side, Stage stage) {
        // Sets the side the player is serving on.
        match.players[player].setServiceSide(side);
        scoreBoardActions(match, tab, matchScoreBoards);
        updateScoreBoard(matchScoreBoards, match, player, tab);
        stage.close();
    }

    // Provides the end game interface after a game is over, giving the players options to start the next game or start a stopwatch.
    public void gameWin(int player, Match match) {

        // Creates a pop up window with application modality.
        Stage gameWinStage = new Stage();
        gameWinStage.initModality(Modality.APPLICATION_MODAL);
        match.setHandout(false);

        // The total number of games  played is the sum of the amount of games each player has won.
        int gameNo = match.players[0].getGameScore() + match.players[1].getGameScore();

        // Sets up the graphic components.
        Button nextGameBtn = new Button("Next Game");
        Button displayStopWatchBtn = new Button("Display stopwatch");
        Label label = new Label(match.players[player].getName() + " won game " + gameNo);
        VBox vBox = new VBox(label, displayStopWatchBtn, nextGameBtn);
        vBox.setSpacing(20);
        vBox.setPadding(new Insets(20));
        vBox.setAlignment(Pos.CENTER);

        nextGameBtn.setOnAction(event -> gameWinStage.close());

        displayStopWatchBtn.setOnAction(event1 -> displayStopWatch(true));

        Scene scene = new Scene(vBox);
        scene.getStylesheets().add(getClass().getResource("Stylesheet.css").toExternalForm());
        scene.setOnKeyPressed(event -> gameWinStage.close());
        gameWinStage.setScene(scene);
        // This means the next game won't start/be shown in the main tabPane until this stage is closed.
        gameWinStage.showAndWait();
    }

    // Provides the interface of confirmation for a player wanting to add a result to Squash Levels.
    public void addMatchToSquashLevels(Match match, Button button) {
        Stage addMatchToSquashLevelsStage = new Stage();
        addMatchToSquashLevelsStage.initModality(Modality.APPLICATION_MODAL);

        BorderPane borderPane = new BorderPane();
        Label label = new Label("The match record is: " + match.getMatchRecord());
        borderPane.setCenter(label);
        label.setStyle("-fx-font-size:20");
        borderPane.setPadding(new Insets(20));

        Button addMatch = new Button("Add match to Squash Levels");
        borderPane.setBottom(addMatch);
        addMatch.setStyle("-fx-font-size:25");
        addMatch.setOnAction(event -> {
            // Creates a 'squashLevelPlayers' array and populates it with the 2 players from the match for parsing to 'resultUpload.main'.
            SquashLevelPlayer[] squashLevelPlayers = new SquashLevelPlayer[2];
            squashLevelPlayers[0] = (SquashLevelPlayer) match.players[0];
            squashLevelPlayers[1] = (SquashLevelPlayer) match.players[1];
            ResultUpload resultUpload = new ResultUpload();
            String jsonReturned = resultUpload.main(squashLevelPlayers, match);
            String[] matchUploadDetails = matchUploadDetails(jsonReturned);
            addMatchToSquashLevelsStage.close();
            squashLevelUploadDetails(matchUploadDetails);
            // Once this the match has been uploaded, it cannot be allowed to be added again. So the button to get to this screen is disabled.
            button.setDisable(true);
        });

        Scene scene = new Scene(borderPane);
        scene.getStylesheets().add(getClass().getResource("Stylesheet.css").toExternalForm());
        addMatchToSquashLevelsStage.setScene(scene);

        addMatchToSquashLevelsStage.showAndWait();
    }

    // Returns a heavily formatted and edited version of the JSON returned from the upload in the form of a String Array.
    public String[] matchUploadDetails(String json) {

        // Only 6 details from the returned JSON from the result upload are relevant to us.
        String[] specificMatchDetails = new String[6];
        // Each separate match detail has a comma in between it. This split allows for easier manipulation of the original string.
        String[] allMatchDetails = json.split(",");

        // Sets some of the match details to a different array to be parsed back to where it was called.
        // All instances of '.substring' are in order to remove unnecessary words and punctuation from the strings.
        specificMatchDetails[0] = allMatchDetails[7];
        specificMatchDetails[0] = specificMatchDetails[0].substring(9, specificMatchDetails[0].length() - 1);

        specificMatchDetails[1] = allMatchDetails[9];
        specificMatchDetails[1] = specificMatchDetails[1].substring(30, specificMatchDetails[1].length() - 1);

        specificMatchDetails[2] = allMatchDetails[10];
        specificMatchDetails[2] = specificMatchDetails[2].substring(14);

        specificMatchDetails[3] = allMatchDetails[16];
        specificMatchDetails[3] = specificMatchDetails[3].substring(30, specificMatchDetails[3].length() - 1);

        specificMatchDetails[4] = allMatchDetails[17];
        specificMatchDetails[4] = specificMatchDetails[4].substring(14);

        specificMatchDetails[5] = allMatchDetails[1];
        specificMatchDetails[5] = specificMatchDetails[5].substring(11, specificMatchDetails[5].length() - 1);

        return specificMatchDetails;
    }

    // Provides the interface for a successful result upload to Squash Levels.
    public void squashLevelUploadDetails(String[] matchDetails) {

        Stage squashLevelsUploadDetailsStage = new Stage();
        squashLevelsUploadDetailsStage.initModality(Modality.APPLICATION_MODAL);

        BorderPane borderPane = new BorderPane();

        borderPane.setTop(new Label(matchDetails[0]));

        HBox hBox1 = new HBox(new Label("Home player level before match: " + matchDetails[1]), new Label("Away player level before match : " + matchDetails[3]));
        HBox hBox2 = new HBox(new Label("Home player level after match: " + matchDetails[2]), new Label("Away player after before match : " + matchDetails[4]));
        hBox1.setSpacing(20);
        hBox1.setAlignment(Pos.CENTER);
        hBox2.setSpacing(20);
        hBox2.setAlignment(Pos.CENTER);

        // This is the URL where the match details will be stored.
        String url = "http://test.squashlevels.com/match_detail.php?match=" + matchDetails[5];
        Label label = new Label("View match at: " + url);
        VBox vBox = new VBox(hBox1, hBox2, label);
        vBox.setSpacing(20);
        vBox.setAlignment(Pos.CENTER);
        borderPane.setCenter(vBox);

        Button leaveBtn = new Button("Leave Screen");
        Button matchPageBtn = new Button("Go to match page");

        HBox hBox = new HBox(matchPageBtn, leaveBtn);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(20);

        leaveBtn.setOnAction(event -> squashLevelsUploadDetailsStage.close());
        matchPageBtn.setOnAction(event -> {
            // Allows the user to click on the Button and be taken the a website on their default browser.
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URL(url).toURI());
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
        leaveBtn.setStyle("-fx-font-size:30");
        leaveBtn.setAlignment(Pos.CENTER);
        borderPane.setBottom(hBox);
        borderPane.setPadding(new Insets(30));

        Scene scene = new Scene(borderPane);
        scene.getStylesheets().add(getClass().getResource("Stylesheet.css").toExternalForm());
        squashLevelsUploadDetailsStage.setScene(scene);

        squashLevelsUploadDetailsStage.showAndWait();
    }

    // Calls the class which displays a stopwatch with modality options.
    public void displayStopWatch(boolean modality) {
        Stage stopWatchStage = new Stage();
        // Some uses of the stopwatch do not require modality
        if (modality) {
            stopWatchStage.initModality(Modality.APPLICATION_MODAL);
        }
        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start(stopWatchStage);
    }

    // Saves the game scoreboard to a .txt file.
    public void saveMatchToTxtFile(MatchScoreBoards matchScoreBoards, Match match) {

        // Creates an instance of BufferedWriter.
        BufferedWriter writeFile;

        // Stores the names and points that each player has won in each game, taken from the gameScoreBoard.
        ArrayList<String> gameScores = new ArrayList<>();

        // Iterates through the contents of 'gameScoreBoard' and assigns the value that each node represents to an
        // element in 'gameScores'.
        for (int i = 0; i < matchScoreBoards.gameScoreBoard.getChildren().size(); i+=2) {
            Label label = (Label) matchScoreBoards.gameScoreBoard.getChildren().get(i);
            gameScores.add(label.getText());
            Label label1 = (Label) matchScoreBoards.gameScoreBoard.getChildren().get(i+1);
            gameScores.add(label1.getText());
        }

        // Writes the contents of 'gameScores' to a text file in two columns. Each player has a column.
        try {
            writeFile = new BufferedWriter(new FileWriter(match.players[0].getName() + " vs " + match.players[1].getName() + ".txt"));
            for (int i = 0; i < gameScores.size(); i += 2) {
                writeFile.write(String.format("%20s %20s \r\n", gameScores.get(i), gameScores.get(i + 1)));
                writeFile.newLine();
            }

            writeFile.close();
            // When the file has been successfully uploaded a pop up window is shown.
            new PopUpWindow("Match Saved Successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
