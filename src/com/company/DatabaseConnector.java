package com.company;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by 12OMarsden on 25/09/2018.
 */
public class DatabaseConnector {

    // Address for school server.
    public static final String connectionUrl1 = "jdbc:sqlserver://ks-sql-02:1433;" +
            "databaseName=DB15;integratedSecurity=true";

    // Address for home server
    public static final String connectionUrl2 = "jdbc:sqlserver://DESKTOP-EID5SI3;" +
            "databaseName=DB15;integratedSecurity=true";

    public static Connection connection;

    // Connects to my database.
    static {
        try {
            connection = DriverManager.getConnection(DatabaseConnector.connectionUrl2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
