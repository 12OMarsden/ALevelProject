package com.company;


public class Match {

    Player[] players = new Player[2];
    private int pointsPerGame;
    private int gamesNeededToWinMatch;
    private String PARorHiHo;
    private String SDor2C;
    private int matchWinner;
    private boolean handout = true;
    private int currentServer;
    private int previousGameWinner;
    private int twoClearCounter;
    private String matchRecord = "";
    private Boolean linkedToSquashLevel = false;


    public Match(String playerA, String playerB, int pointsPerGame, int gamesNeededToWinMatch, String PARorHiHo, String SDor2C, SquashLevelPlayer[] selectedPlayers) {


        this.pointsPerGame = pointsPerGame;
        this.gamesNeededToWinMatch = gamesNeededToWinMatch;
        this.PARorHiHo = PARorHiHo;
        this.SDor2C = SDor2C;

        // If no 'SquashLevelPlayers' are parsed then the match can't be linked to Squash Levels.
        if (selectedPlayers[0] != null && selectedPlayers[1] != null) {
            this.linkedToSquashLevel = true;
            SquashLevelPlayer slPlayer1 = selectedPlayers[0];
            SquashLevelPlayer slPlayer2 = selectedPlayers[1];
            players[0] = slPlayer1;
            players[1] = slPlayer2;
//            players[0].setSquashLevelsPlayer(selectedPlayers[0]);
//            players[1].setSquashLevelsPlayer(selectedPlayers[1]);
        }else {
            Player player1 = new Player();
            Player player2 = new Player();

            players[0] = player1;
            players[1] = player2;

            players[0].setName(playerA);
            players[1].setName(playerB);
        }


    }

    // Compares a players score at any point with the amount required to win a game.
    public boolean checkGameWon(int i) {
        if (players[i].getPointScore() == pointsPerGame) {
            matchRecord += (players[0].getPointScore() + "-" + players[1].getPointScore() + "|");
            // If 2C is selected, these values need to be reset to their original values so that they work for the next match.
            pointsPerGame = pointsPerGame - twoClearCounter;
            twoClearCounter = 0;
            return true;
        } else {
            return false;
        }
    }

    // Sets pointsPerGame to always be 2 more than the losing player has.
    public void checkFor2C() {
        if (players[0].getPointScore() == pointsPerGame - 1 && players[1].getPointScore() == pointsPerGame - 1) {
            pointsPerGame++;
            twoClearCounter++;
        }
    }

    // Compares a players game score at any point with the amount required to win a match.
    public boolean checkMatchWon(int i) {
        if (players[i].getGameScore() == gamesNeededToWinMatch) {
            matchWinner = i;
            matchRecord = matchRecord.substring(0, matchRecord.length() - 1);
            return true;
        } else {
            return false;
        }
    }

    // Sets both players point scores back to zero/
    public void resetPoints() {
        players[0].setPointScore(0);
        players[1].setPointScore(0);
    }

    // Adds a point to the referenced player and checks and sets handout status.
    public void addPoints(int player) {
        if (currentServer != player) {
            handout = true;
        }
        // In HiHo ('HandIn-HandOut') scoring, a player can only win a point if they win a rally off of a serve.
        if (PARorHiHo.equals("HiHo")&&handout!=true){
            players[player].setPointScore(players[player].getPointScore() + 1);
        }else if (PARorHiHo.equals("PAR")){
            players[player].setPointScore(players[player].getPointScore() + 1);
        }
    }

    public String getMatchRecord() {
        return matchRecord;
    }

    public void setCurrentServer(int currentServer) {
        this.currentServer = currentServer;
    }

    public int getCurrentServer(){
        return currentServer;
    }

    public Boolean getLinkedToSquashLevel() {
        return linkedToSquashLevel;
    }

    public int getPreviousGameWinner() {
        return previousGameWinner;
    }

    public int getMatchWinner() {

        return matchWinner;
    }

    public String getSDor2C() {

        return SDor2C;
    }

    public String getPARorHiHo() {
        return PARorHiHo;
    }

    public void setPreviousGameWinner(int previousGameWinner) {
        this.previousGameWinner = previousGameWinner;
    }

    public boolean getHandout() {
        return handout;
    }

    public void setHandout(boolean handout) {
        this.handout = handout;
    }

}