package com.company;

/**
 * Created by 12OMarsden on 13/02/2019.
 */
public class SquashLevelPlayer extends Player {

    int playerId;
    String country;
    String county;
    String matchType;
    int events;
    int level;
    int confidence;

    // Constructs 'SquashLevelPlayer' with the details retrieved from the Squash Levels DB.
    public SquashLevelPlayer(String[] details) {

        //'.substring(1,details[1].length()-1)' is necessary to delete unnecessary quotation marks left over from the first formatting stage.
        this.setName(details[1].substring(1,details[1].length()-1));
        this.playerId = Integer.parseInt(details[2]);
        this.country = details[3].substring(1,details[3].length()-1);
        this.county = details[5].substring(1,details[5].length()-1);
        this.matchType = details[7].substring(1,details[7].length()-1);
        this.events = Integer.parseInt(details[9]);
        this.level = Integer.parseInt(details[12]);
        String formattedDetails = details[13].substring(1,2);
        this.confidence = Integer.parseInt(formattedDetails);
    }
}

