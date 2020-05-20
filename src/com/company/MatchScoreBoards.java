package com.company;

import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;

public class MatchScoreBoards {
    GridPane pointScoreBoard;
    GridPane gameScoreBoard;
    ScrollPane scrollPane;
    Button left, right;

    public MatchScoreBoards(GridPane pointScoreBoard, GridPane gameScoreBoard, ScrollPane scrollPane, Button left, Button right) {
        this.pointScoreBoard = pointScoreBoard;
        this.gameScoreBoard = gameScoreBoard;
        this.scrollPane = scrollPane;
        this.left = left;
        this.right = right;
    }
}
