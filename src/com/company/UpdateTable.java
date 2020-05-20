package com.company;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by 12OMarsden on 02/10/2018.
 */
public class UpdateTable {

    // Returns a string array list of all elements in the 'players' table.
    public ArrayList<String> UpdateTable() {

        ArrayList<String> accountDetails = new ArrayList<>();

        Statement stmt;
        ResultSet rs;

        try {
            // Create and execute an SQL statement that returns all data in 'players'.
            String SQL = "SELECT * FROM players";
            stmt = DatabaseConnector.connection.createStatement();
            rs = stmt.executeQuery(SQL);

            while (rs.next()) {
                // Counter for row value for populating the grid pane table
                String username = (rs.getString(rs.findColumn("username")));
                String password = (rs.getString(rs.findColumn("password")));

                accountDetails.add(username+","+password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accountDetails;
    }
}
