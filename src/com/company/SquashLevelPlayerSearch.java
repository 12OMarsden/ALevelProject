package com.company;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.json.JSONException;

// Adapted version of code found at:
// https://stackoverflow.com/questions/4308554/simplest-way-to-read-json-from-a-url-in-java
public class SquashLevelPlayerSearch {

    // Returns an array list of 'SquashLevelPlayer' generated from a URL.
    public ArrayList<SquashLevelPlayer> main(String searchItem) throws IOException, JSONException {

        // Replaces each space with '%20' so an accurate URL is created.
        if (searchItem.contains(" ")) {
            searchItem = searchItem.replace(" ", "%20");
        }
        // Sets up the URL.
        String url = "http://test.squashlevels.com/players.php?search=" + searchItem + "&format=json&SL-client=OM1.0";
        // Retrieves a String array list from what the URL returns.
        ArrayList<String> JSONText = readJsonFromUrl(url);

        // All lines that equal ",{" do not need to be processed so these need to be identified.
        int[] linesToBeDeleted = new int[JSONText.size()];
        int counter = 0;
        for (int i = 0; i < JSONText.size(); i++) {
            String line = JSONText.get(i);
            if (line.equals(",{")) {
                linesToBeDeleted[counter] = i;
                counter++;
            }
        }
        // As array lists are dynamic, the unnecessary records in JSON text must be deleted in reverse order so that
        // the linesToBeDeleted array is still accurate.
        for (int i = linesToBeDeleted.length - 1; i >= 0; i--) {
            if (linesToBeDeleted[i] != 0) {
                JSONText.remove(linesToBeDeleted[i]);
            }
        }

        //Deletes first 2 and second to last element of the retrieved text as they are unnecessary.
        JSONText.remove(0);
        JSONText.remove(0);
        try {
            JSONText.remove(JSONText.size() - 1);
        }catch (ArrayIndexOutOfBoundsException e){
            new PopUpWindow("No names were found");
        }

        // Iterates through each element in JSONText, parses it to lineFormat and adds what is returned to an array list
        // of SquashLevelPlayer
        ArrayList<SquashLevelPlayer> squashLevelPlayerArrayList = new ArrayList<>();
        for (String n : JSONText) {
            squashLevelPlayerArrayList.add(lineFormat(n));
        }

        return squashLevelPlayerArrayList;

    }

    // Returns an easy to process version of the JSON from the URL.
    public ArrayList<String> readJsonFromUrl(String url) throws IOException, JSONException {

        // Opens the URL and retrieves the JSON as a long chain of ASCII values.
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            ArrayList<String> JSONText = readAll(rd);
            return JSONText;
        } finally {
            is.close();
        }
    }

    // Converts the ASCII values from the JSON to chars and returns a String array list holding all of these chars;
    // split by '{' and '}'.
    private ArrayList<String> readAll(Reader rd) throws IOException {

        // Using the char that is represented by each ASCII value it adds all the chars that it reads before reading in
        // a '{' or '}' to jsonLine. Once a '{' or '}' is found jsonLine is added to a String Array then reset back to
        // nothing.
        String jsonLine = "";
        int cp;
        ArrayList<String> JSONText = new ArrayList<>();
        while ((cp = rd.read()) != -1) {
            jsonLine += ((char) cp);
            if (cp == '{' || cp == '}') {
                JSONText.add(jsonLine);
                jsonLine = "";
            }
        }
        return JSONText;
    }

    // Processes one element from JSONText at a time into an instance of 'SquashLevelPlayer'.
    public SquashLevelPlayer lineFormat(String squashLevelData) {

        String[] allDetails = squashLevelData.split(",");
        // This array is 14 large as each string will always have 14 sections to them.
        String[] individualDetails = new String[14];

        for (int i = 0; i < allDetails.length; i++) {
            // Removes the text that identifies the value proceeding it.
            // (e.g. "playerid":25614 => 25461)
            individualDetails[i] = allDetails[i].substring(allDetails[i].indexOf(":") + 1);

            if (i == 14) {
                //Creates an instance of SquashLevelPlayer using the details that have been processed.
                SquashLevelPlayer squashLevelPlayers = new SquashLevelPlayer(individualDetails);
                return squashLevelPlayers;
            }
        }

        //This would only ever be run instead of the first return if no players were returned from the search.
        return new SquashLevelPlayer(individualDetails);

    }
}