package com.company;

import java.sql.Statement;

public class SQLQueryExecution {

    // Will execute a given SQL statement that returns some data.
    public SQLQueryExecution(String SQL){
        Statement stmt;
        try {

            stmt = DatabaseConnector.connection.createStatement();
            stmt.executeUpdate(SQL);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}


