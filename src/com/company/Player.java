package com.company;

// Stores player details at any point in the match such as point score.
public class Player {

    private String name;
    private int pointScore=0;
    private int gameScore=0;
    private char serviceSide;


    public char getServiceSide() {
        return serviceSide;
    }

    public void setServiceSide(char serviceSide) {
        this.serviceSide = serviceSide;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getPointScore() {
        return pointScore;
    }

    public void setPointScore(int pointScore) {
        this.pointScore = pointScore;
    }

    public int getGameScore() {
        return gameScore;
    }

    public void setGameScore(int gameScore) {
        this.gameScore = gameScore;
    }

}
