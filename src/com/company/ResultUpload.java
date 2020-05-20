package com.company;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 12OMarsden on 27/02/2019.
 */
public class ResultUpload {

    // Code adapted for suitable use from: 'https://stackoverflow.com/questions/40494871/send-post-data-with-java'.
    // Connects to a URl a posts the match details to it.
    public static String main(SquashLevelPlayer[] squashLevelPlayers, Match match) {

        int key = getCheckKey();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String jsonReturned = "";

        try {
            // The url of the part of the site which handles uploads.
            URL url = new URL("http://test.squashlevels.com/info.php");
            Map<String, Object> params = new LinkedHashMap<>();
            // Parameter details found at: 'https://www.squashlevels.com/doc.php?doc=connecting.htm' under: 'Posting a singles match straight in to the DB' (not accessible to non authorised users).
            params.put("action", "add_result");
            params.put("key", key);
            params.put("datetime", dateFormat.format(date));
            params.put("homeplayer_name", squashLevelPlayers[0].getName());
            params.put("homeplayer_id", squashLevelPlayers[0].playerId);
            params.put("homeplayer_country", squashLevelPlayers[0].country);
            params.put("awayplayer_name", squashLevelPlayers[1].getName());
            params.put("awayplayer_id", squashLevelPlayers[1].playerId);
            params.put("awayplayer_country", squashLevelPlayers[1].country);//
            params.put("scores", match.getMatchRecord());
            params.put("matchtype", "Kennet Boxes");
            params.put("source_name", "squash_marking_tool");
            params.put("process", "1");
            params.put("SL-client", "SMT1.0");

            StringBuilder postData = new StringBuilder();
            // This creates the key & value pairs.
            // E.g key: action. value: add_result.
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0)
                    postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }

            // Converts string to byte array, as it should be sent.
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            // Connects to the url.
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Tells the server that this is POST and in which format is the data
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);

            // Writes the key-value pairs to the url.
            conn.getOutputStream().write(postDataBytes);

            // This gets the output from your server
            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            // Adds each character from the returned JSON to 'jsonReturned' to produce a string of everything returned.
            for (int c; (c = in.read()) >= 0; ) {
                char d = (char)c;
                jsonReturned += d;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonReturned;

    }

    // Retrieves the current time key.
    public static int getCheckKey() {
        int key;
        //Found at https://stackoverflow.com/questions/732034/getting-unixtime-in-java
        //This finds the current unix timestamp and does the necessary maths to produce the key check
        long unixTime = (System.currentTimeMillis() / 1000L);
        key = (int) (Math.sqrt(unixTime * 100)) - 100;
        return key;
    }

}
