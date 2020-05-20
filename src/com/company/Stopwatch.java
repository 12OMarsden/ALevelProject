package com.company;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Stopwatch extends Application{

    // Code adapted from: 'https://gist.github.com/SatyaSnehith/167779aac353b4e79f8dfae4ed23cb85'.
    Scene scene;
    VBox vBox;
    HBox hBox;
    Button stopStartButton, resetButton, closeButton;
    Text text;
    Timeline timeline;
    int mins = 0, secs = 0, millis = 0;
    boolean sos = true;

    public static void main(String[] args) {
        launch(args);
    }

    void change(Text text) {
        if(millis == 1000) {
            secs++;
            millis = 0;
        }
        if(secs == 60) {
            mins++;
            secs = 0;
        }
        text.setText((((mins/10) == 0) ? "0" : "") + mins + ":"
                + (((secs/10) == 0) ? "0" : "") + secs + ":"
                + (((millis/10) == 0) ? "00" : (((millis/100) == 0) ? "0" : "")) + millis++);
    }

    @Override
    public void start(Stage stage) {
        text = new Text("00:00:000");
        timeline = new Timeline(new KeyFrame(Duration.millis(1), event -> {
            change(text);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(false);
        stopStartButton = new Button("Start");
        stopStartButton.setOnAction(event -> {
            if (sos) {
                timeline.play();
                sos = false;
                stopStartButton.setText("Stop");
            } else {
                timeline.pause();
                sos = true;
                stopStartButton.setText("Start");
            }
        });
        resetButton = new Button("Reset");
        resetButton.setOnAction(event -> {
            mins = 0;
            secs = 0;
            millis = 0;
            timeline.pause();
            text.setText("00:00:000");
            if(!sos) {
                sos = true;
                stopStartButton.setText("Start");
            }
        });
        closeButton = new Button("Close");
        closeButton.setOnAction(event -> stage.close());
        hBox = new HBox(30);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(stopStartButton, resetButton);
        vBox = new VBox(30);
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(text, hBox, closeButton);
        scene = new Scene(vBox, 200, 150);
        scene.getStylesheets().add(getClass().getResource("Stylesheet.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Stopwatch");
        stage.showAndWait();
    }
}
